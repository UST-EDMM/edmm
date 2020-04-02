package io.github.edmm.plugins.cfn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.scaleset.cfbuilder.cloudformation.Authentication;
import com.scaleset.cfbuilder.core.Fn;
import com.scaleset.cfbuilder.core.Module;
import com.scaleset.cfbuilder.core.Parameter;
import com.scaleset.cfbuilder.core.Template;
import com.scaleset.cfbuilder.ec2.Instance;
import com.scaleset.cfbuilder.ec2.SecurityGroup;
import com.scaleset.cfbuilder.ec2.UserData;
import com.scaleset.cfbuilder.ec2.metadata.CFNInit;
import com.scaleset.cfbuilder.iam.Role;
import io.github.edmm.core.transformation.TransformationException;
import io.github.edmm.model.component.Compute;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.utils.Consts;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.github.edmm.plugins.cfn.CloudFormationUtils.getUserDataFn;

@Getter
@Setter
public class CloudFormationModule extends Module {

    private static final Logger logger = LoggerFactory.getLogger(CloudFormationModule.class);

    public static final String SECURITY_GROUP = "_security_group";
    public static final String IP_OPEN = "0.0.0.0/0";
    public static final String PROTOCOL_TCP = "tcp";
    public static final String CONFIG_SETS = "LifecycleOperations";
    public static final String CONFIG_INIT = "Init";
    public static final String CONFIG_CREATE = "Create";
    public static final String CONFIG_CONFIGURE = "Configure";
    public static final String CONFIG_START = "Start";
    public static final String KEY_NAME = "KeyName";
    public static final String KEY_NAME_DESCRIPTION = "Name of an existing EC2 key pair to enable SSH access to the instances";
    public static final String KEY_NAME_TYPE = "AWS::EC2::KeyPair::KeyName";
    public static final String KEY_NAME_CONSTRAINT_DESCRIPTION = "Must be the name of an existing EC2 key pair";
    public static final String MODE_777 = "000777";
    public static final String OWNER_GROUP_ROOT = "root";

    private final String region;
    private final Object keyNameVar;
    private final String stackName = CloudFormationUtils.getRandomStackName();
    private final String bucketName = CloudFormationUtils.getRandomBucketName();
    private final Set<String> computeResources = new HashSet<>();
    private final Map<String, Set<Number>> portMapping = new HashMap<>();
    private final Map<String, CFNInit> operationsMapping = new HashMap<>();
    private final List<Pair<RootComponent, RootComponent>> connectionPairs = new ArrayList<>();
    private final Map<String, Map<String, Object>> envVars = new HashMap<>();
    private final Map<String, Fn> fnMapping = new HashMap<>();

    private boolean keyPair;

    public CloudFormationModule(String region) {
        id(Consts.EMPTY);
        template(new Template());
        this.region = region;
        this.keyNameVar = template.ref(KEY_NAME);
    }

    public void addComputeResource(Compute compute) {
        computeResources.add(compute.getNormalizedName());
    }

    public boolean containsComputeResource(Compute compute) {
        return computeResources.contains(compute.getNormalizedName());
    }

    public void addPortMapping(RootComponent component, Number port) {
        String name = component.getNormalizedName();
        Set<Number> ports = portMapping.get(name);
        if (ports == null) {
            ports = new HashSet<>();
        }
        ports.add(port);
        portMapping.put(name, ports);
    }

    public void addOperationsMapping(Compute compute, CFNInit operations) {
        operationsMapping.put(compute.getNormalizedName(), operations);
    }

    public CFNInit getOperations(Compute compute) {
        String name = compute.getNormalizedName();
        CFNInit operations = operationsMapping.get(name);
        if (operations == null) {
            operations = new CFNInit(CONFIG_SETS);
            addOperationsMapping(compute, operations);
        }
        return operations;
    }

    public Optional<CFNInit> getOperations(String name) {
        return Optional.of(operationsMapping.get(name));
    }

    public void addConnectionPair(Pair<RootComponent, RootComponent> pair) {
        connectionPairs.add(pair);
    }

    public void addEnvVar(Compute compute, String name, Object value) {
        String computeName = compute.getNormalizedName();
        envVars.computeIfAbsent(computeName, k -> new HashMap<>());
        envVars.get(computeName).put(name, value);
    }

    public void addFn(String key, Fn fn) {
        fnMapping.put(key, fn);
    }

    public boolean containsFn(String key) {
        return fnMapping.containsKey(key);
    }

    public Fn getFn(String key) {
        return fnMapping.get(key);
    }

    public Template getTemplate() {
        return template;
    }

    public Map<String, Parameter> getParameters() {
        return template.getParameters();
    }

    @Override
    public String toString() {
        try {
            build();
            return template.toString(true);
        } catch (Exception e) {
            logger.error("Failed to build YAML", e);
            throw new TransformationException(e);
        }
    }

    @Override
    public void build() {
        if (keyPair) {
            strParam(KEY_NAME)
                .type(KEY_NAME_TYPE)
                .description(KEY_NAME_DESCRIPTION)
                .constraintDescription(KEY_NAME_CONSTRAINT_DESCRIPTION);
        }
        portMapping.forEach((name, ports) -> {
            SecurityGroup securityGroup = (SecurityGroup) getResource(name + SECURITY_GROUP);
            securityGroup.ingress(ingress -> ingress.cidrIp(IP_OPEN), PROTOCOL_TCP, ports.toArray());
        });
        operationsMapping.forEach((name, operations) -> {
            Instance compute = (Instance) this.getResource(name);
            if (!operations.getConfigs().isEmpty()) {
                compute.addCFNInit(operations).userData(new UserData(getUserDataFn(this.getTemplate(), name, CONFIG_SETS)));
            }
        });
        // Enable S3 auth for all EC2 instances
        Role s3Role = CloudFormationUtils.getS3Role(this);
        CloudFormationUtils.getS3Policy(this).roles(s3Role);
        CloudFormationUtils.getS3InstanceProfile(this).roles(s3Role);
        Authentication s3auth = CloudFormationUtils.getS3Authentication(this);
        for (String name : computeResources) {
            Instance instance = (Instance) this.getResource(name);
            instance.authentication(s3auth).iamInstanceProfile(ref("InstanceProfile"));
        }
    }
}
