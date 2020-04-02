package io.github.edmm.plugins.cfn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.scaleset.cfbuilder.cloudformation.Authentication;
import com.scaleset.cfbuilder.core.Fn;
import com.scaleset.cfbuilder.core.Template;
import com.scaleset.cfbuilder.iam.InstanceProfile;
import com.scaleset.cfbuilder.iam.Policy;
import com.scaleset.cfbuilder.iam.PolicyDocument;
import com.scaleset.cfbuilder.iam.Principal;
import com.scaleset.cfbuilder.iam.Role;
import com.scaleset.cfbuilder.iam.Statement;
import io.github.edmm.utils.Consts;

import static com.scaleset.cfbuilder.core.Ref.ref;

public abstract class CloudFormationUtils {

    private static final String[] USER_DATA_PARAMS = {
        "#!/bin/bash -xe\n",
        "mkdir -p /tmp/aws-cfn-bootstrap-latest\n",
        "curl https://s3.amazonaws.com/cloudformation-examples/aws-cfn-bootstrap-latest.tar.gz | tar xz -C /tmp/aws-cfn-bootstrap-latest --strip-components 1\n",
        "apt-get update\n",
        "DEBIAN_FRONTEND=noninteractive apt-get upgrade -yq\n",
        "apt-get -y install python-setuptools\n",
        "easy_install /tmp/aws-cfn-bootstrap-latest\n",
        "cp /tmp/aws-cfn-bootstrap-latest/init/ubuntu/cfn-hup /etc/init.d/cfn-hup\n",
        "chmod 755 /etc/init.d/cfn-hup\n",
        "update-rc.d cfn-hup defaults\n",
        "# Install files and packages from metadata\n",
        "/usr/local/bin/cfn-init -v ",
        "         --stack "
    };

    public static String getRandomBucketName() {
        return "edmm-bucket-" + UUID.randomUUID();
    }

    public static String getRandomStackName() {
        return "edmm-stack-" + UUID.randomUUID();
    }

    public static String normalize(String input) {
        return input.replaceAll("[^A-Za-z0-9]", "");
    }

    public static Fn getUserDataFn(Template template, String resource, String configsets) {
        Object[] userdata = {
            template.ref("AWS::StackName"),
            "         --resource " + resource + " ",
            "         --configsets " + configsets + " ",
            "         --region ",
            template.ref("AWS::Region"),
            "\n",
            "/usr/local/bin/cfn-signal -e $? ",
            "         --stack ",
            template.ref("AWS::StackName"),
            "         --resource " + resource + " ",
            "         --region ",
            template.ref("AWS::Region"),
            "\n"
        };
        List<Object> params = new ArrayList<>();
        Collections.addAll(params, USER_DATA_PARAMS);
        Collections.addAll(params, userdata);
        return Fn.fnDelimiter("Join", Consts.EMPTY, params.toArray());
    }

    public static Authentication getS3Authentication(CloudFormationModule module) {
        return new Authentication("S3Creds").addBucket(module.getBucketName()).roleName(ref("InstanceRole")).type("S3");
    }

    public static Policy getS3Policy(CloudFormationModule module) {
        Statement statement = new Statement().addAction("s3:GetObject").effect("Allow")
            .addResource("arn:aws:s3:::" + module.getBucketName() + "/*");
        PolicyDocument policyDocument = new PolicyDocument().addStatement(statement);
        return module.resource(Policy.class, "RolePolicies").policyName("S3Download").policyDocument(policyDocument);
    }

    public static Role getS3Role(CloudFormationModule module) {
        List<String> resourceList = new ArrayList<>();
        resourceList.add("ec2.amazonaws.com");
        Principal principal = new Principal().principal("Service", resourceList);
        Statement statement = new Statement().addAction("sts:AssumeRole").effect("Allow").principal(principal);
        PolicyDocument policyDocument = new PolicyDocument().addStatement(statement);
        return module.resource(Role.class, "InstanceRole").path("/").assumeRolePolicyDocument(policyDocument);
    }

    public static InstanceProfile getS3InstanceProfile(CloudFormationModule module) {
        return module.resource(InstanceProfile.class, "InstanceProfile").path("/");
    }
}
