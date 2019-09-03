package io.github.edmm.plugins.cfn;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.amazonaws.auth.AWSCredentials;
import com.scaleset.cfbuilder.core.Fn;
import com.scaleset.cfbuilder.core.Module;
import com.scaleset.cfbuilder.core.Parameter;
import com.scaleset.cfbuilder.core.Template;
import com.scaleset.cfbuilder.ec2.metadata.CFNInit;
import io.github.edmm.core.plugin.PluginFileAccess;
import io.github.edmm.core.transformation.TransformationException;
import io.github.edmm.model.component.Compute;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
@Setter
public class CloudFormationModule extends Module {

    private static final Logger logger = LoggerFactory.getLogger(CloudFormationModule.class);

    //    public static final String CONFIG_SETS = "LifecycleOperations";
//    public static final String CONFIG_CREATE = "Create";
//    public static final String CONFIG_CONFIGURE = "Configure";
//    public static final String CONFIG_START = "Start";
    public static final String SECURITY_GROUP = "_security_group";
//    public static final String ABSOLUTE_FILE_PATH = "/opt/";
//    public static final String URL_HTTP = "http://";
//    public static final String URL_S3_AMAZONAWS = ".s3.amazonaws.com";
//    public static final String FILEPATH_TARGET = OUTPUT_DIR + "files/";
//    public static final String MODE_500 = "000500";
//    public static final String MODE_644 = "000644";
//    public static final String OWNER_GROUP_ROOT = "root";
//    public static final String FILEPATH_NODEJS_CREATE = "create-nodejs.sh";

    private static final String KEY_NAME = "KeyName";
    private static final String KEY_NAME_DESCRIPTION = "Name of an existing EC2 key pair to enable SSH access to the instances";
    private static final String KEY_NAME_TYPE = "AWS::EC2::KeyPair::KeyName";
    private static final String KEY_NAME_CONSTRAINT_DESCRIPTION = "Must be the name of an existing EC2 key pair";

    private PluginFileAccess fileAccess;
    private String stackName;
    private String bucketName;
    private String awsRegion;
    private AWSCredentials awsCredentials;
    private Object keyNameVar;
    private boolean keyPair;
    private Set<String> computeSet;
    private Map<String, CFNInit> cfnInitMap;
    private Map<String, Fn> fnSaver;
    private Set<String> authenticationSet;
    private Map<String, Map<String, String>> environmentMap;

    public CloudFormationModule(PluginFileAccess fileAccess, String awsRegion, AWSCredentials awsCredentials) {
        this.id("").template(new Template());
        this.fileAccess = fileAccess;
        this.stackName = CloudFormationUtils.getRandomStackName();
        this.bucketName = CloudFormationUtils.getRandomBucketName();
        this.awsRegion = awsRegion;
        this.awsCredentials = awsCredentials;
        this.keyNameVar = template.ref(KEY_NAME);
        this.computeSet = new HashSet<>();
        this.cfnInitMap = new HashMap<>();
        this.fnSaver = new HashMap<>();
        this.authenticationSet = new HashSet<>();
        this.environmentMap = new HashMap<>();
    }

    public boolean contains(Compute compute) {
        return computeSet.contains(compute.getNormalizedName());
    }

    public Template getTemplate() {
        return this.template;
    }

    public Map<String, Parameter> getParameters() {
        return this.template.getParameters();
    }

    @Override
    public String toString() {
        try {
            this.build();
            return this.template.toString(true);
        } catch (Exception e) {
            logger.error("Failed to build YAML", e);
            throw new TransformationException(e);
        }
    }

    @Override
    public void build() {
        if (this.keyPair) {
            strParam(KEY_NAME)
                    .type(KEY_NAME_TYPE)
                    .description(KEY_NAME_DESCRIPTION)
                    .constraintDescription(KEY_NAME_CONSTRAINT_DESCRIPTION);
        }
    }
}
