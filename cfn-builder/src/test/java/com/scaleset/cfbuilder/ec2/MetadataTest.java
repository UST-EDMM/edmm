package com.scaleset.cfbuilder.ec2;

import java.util.ArrayList;
import java.util.List;

import com.scaleset.cfbuilder.cloudformation.Authentication;
import com.scaleset.cfbuilder.core.Fn;
import com.scaleset.cfbuilder.core.Module;
import com.scaleset.cfbuilder.core.Template;
import com.scaleset.cfbuilder.ec2.metadata.CFNCommand;
import com.scaleset.cfbuilder.ec2.metadata.CFNFile;
import com.scaleset.cfbuilder.ec2.metadata.CFNInit;
import com.scaleset.cfbuilder.ec2.metadata.CFNPackage;
import com.scaleset.cfbuilder.ec2.metadata.CFNService;
import com.scaleset.cfbuilder.ec2.metadata.Config;
import com.scaleset.cfbuilder.ec2.metadata.SimpleService;
import com.scaleset.cfbuilder.iam.InstanceProfile;
import com.scaleset.cfbuilder.iam.Policy;
import com.scaleset.cfbuilder.iam.PolicyDocument;
import com.scaleset.cfbuilder.iam.Principal;
import com.scaleset.cfbuilder.iam.Role;
import com.scaleset.cfbuilder.iam.Statement;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertNotNull;

public class MetadataTest {

    @Test
    public void metadataTest() {
        Template lampTemplate = new Template();
        new MetadataModule().id("").template(lampTemplate).build();

        assertNotNull(lampTemplate);
        System.err.println(lampTemplate.toString(true));
    }

    @Test
    public void fileTest() {
        //ensure that a CFNFile can only take Source or File
        Template fileTemplate = new Template();
        new FileModule().id("").template(fileTemplate).build();

        assertNotNull(fileTemplate);
        String templateString = fileTemplate.toString(true);
        Assert.assertThat(templateString, containsString("source:"));
        Assert.assertThat(templateString, not(containsString("content:")));

        System.err.println(templateString);
    }

    class MetadataModule extends Module {
        private static final String KEYNAME_DESCRIPTION = "Name of an existing EC2 KeyPair to enable SSH access to " +
                "the instances";
        private static final String KEYNAME_TYPE = "AWS::EC2::KeyPair::KeyName";
        private static final String KEYNAME_CONSTRAINT_DESCRIPTION = "must be the name of an existing EC2 KeyPair.";

        private static final String CFNINIT_CONFIGSET = "InstallAndRun";
        private static final String CFNINIT_CONFIG_INSTALL = "Install";
        private static final String CFNINIT_CONFIG_CONFIGURE = "Configure";

        public void build() {

            Object keyName = option("KeyName").orElseGet(
                    () -> strParam("KeyName").type(KEYNAME_TYPE).description(KEYNAME_DESCRIPTION)
                            .constraintDescription(KEYNAME_CONSTRAINT_DESCRIPTION));

            Object cidrIp = "0.0.0.0/0";
            Object keyNameVar = template.ref("KeyName");

            CFNPackage cfnPackage = new CFNPackage("apt")
                    .addPackage("apache2")
                    .addPackage("php")
                    .addPackage("libapache2-mod-php7.0");

            CFNFile indexFile = new CFNFile("/var/www/html/index.php")
                    .setContent("<php?\nphpinfo()\n?>")
                    .setEncoding("base64")
                    .setMode("000644")
                    .setOwner("www-data")
                    .setGroup("www-data");

            CFNFile indexFileWithUrl = new CFNFile("/var/www/html/index2.php")
                    .setSource("https://s3-us-west-2.amazonaws.com/link/to/file")
                    .setMode("000644")
                    .setOwner("www-data")
                    .setGroup("www-data");

            CFNService service = new CFNService().addService(new SimpleService("apache2")
                    .setEnabled(true)
                    .setEnsureRunning(true)
                    .addPackage("apt", "libapache2-mod-php7.0"));

            Config install = new Config(CFNINIT_CONFIG_INSTALL)
                    .putFile(indexFile)
                    .putFile(indexFileWithUrl)
                    .putPackage(cfnPackage)
                    .putService(service);

            CFNCommand configure_mysql = new CFNCommand("configure_myphp", "sh /tmp/configure_myphpapp.sh")
                    .addEnv("database_name", "mydatabase");
            CFNInit cfnInit = new CFNInit(CFNINIT_CONFIGSET)
                    .addConfig(CFNINIT_CONFIGSET, install);

            cfnInit.getOrAddConfig(CFNINIT_CONFIGSET, CFNINIT_CONFIG_CONFIGURE)
                    .putCommand(configure_mysql);

            SecurityGroup webServerSecurityGroup = resource(SecurityGroup.class, "WebServerSecurityGroup")
                    .groupDescription("Enable ports 80 and 22")
                    .ingress(ingress -> ingress.cidrIp(cidrIp), "tcp", 80, 22);

            Object groupId = webServerSecurityGroup.fnGetAtt("GroupId");

            Role instanceRole = resource(Role.class, "InstanceRole")
                    .path("/")
                    .assumeRolePolicyDocument("PolicyContent");

            List<String> resourceList = new ArrayList<>();
            resourceList.add("ec2.amazonaws.com");

            Principal principal = new Principal().principal("Service", resourceList);

            Statement policyDocumentStatement = new Statement()
                    .addAction("s3:GetObject")
                    .effect("Allow")
                    .addResource("bucketName")
                    .principal(principal);

            PolicyDocument policyDocument = new PolicyDocument()
                    .version("1.0")
                    .addStatement(policyDocumentStatement);

            Policy rolePolicies = resource(Policy.class, "RolePolicies")
                    .policyName("S3Download")
                    .policyDocument(policyDocument)
                    .roles(instanceRole)
                    .groups("group")
                    .users("user");

            InstanceProfile instanceProfile = resource(InstanceProfile.class, "InstanceProfile")
                    .path("/")
                    .roles(instanceRole)
                    .instanceProfileName("InstanceProfileName");

            Authentication authentication = new Authentication("S3Creds")
                    .accessKeyId("123")
                    .addBucket("bucketName")
                    .roleName(ref("InstanceRole"))
                    .type("S3")
                    .addUri("www.com")
                    .secretKey("secret")
                    .username("user");

            Instance webServerInstance = resource(Instance.class, "WebServerInstance")
                    .addCFNInit(cfnInit)
                    .imageId("ami-0def3275")
                    .instanceType("t2.micro")
                    .securityGroupIds(webServerSecurityGroup)
                    .keyName(keyNameVar)
                    .userData(new UserData(Fn.fnDelimiter("Join", "", "one", "two")))
                    .iamInstanceProfile(instanceProfile)
                    .authentication(authentication);

            ArrayList<String> bucketList = new ArrayList<String>();
            bucketList.add("bucketName");

            ArrayList<String> uriList = new ArrayList<String>();
            uriList.add("www.com");

            Authentication authentication2 = new Authentication("S3Creds")
                    .name("AltCreds")
                    .buckets(bucketList)
                    .uris(uriList)
                    .deleteBucket("bucketName")
                    .deleteUri("www.com")
                    .addBucket("bucketName2")
                    .addBucket("bucketName3")
                    .addUri("www.org")
                    .addUri("www.tv");

            Instance otherInstance = resource(Instance.class, "OtherInstance")
                    .authentication(authentication2);

            Object publicDNSName = webServerInstance.fnGetAtt("PublicDnsName");

            output("websiteURL", publicDNSName, "URL for newly created LAMP stack");
        }
    }

    class FileModule extends Module {
        private static final String KEYNAME_DESCRIPTION = "Name of an existing EC2 KeyPair to enable SSH access to " +
                "the instances";
        private static final String KEYNAME_TYPE = "AWS::EC2::KeyPair::KeyName";
        private static final String KEYNAME_CONSTRAINT_DESCRIPTION = "must be the name of an existing EC2 KeyPair.";

        private static final String CFNINIT_CONFIGSET = "InstallAndRun";
        private static final String CFNINIT_CONFIG_INSTALL = "Install";

        public void build() {

            Object keyName = option("KeyName").orElseGet(
                    () -> strParam("KeyName").type(KEYNAME_TYPE).description(KEYNAME_DESCRIPTION)
                            .constraintDescription(KEYNAME_CONSTRAINT_DESCRIPTION));

            Object cidrIp = "0.0.0.0/0";
            Object keyNameVar = template.ref("KeyName");

            CFNFile indexFile = new CFNFile("/var/www/html/index.php")
                    .setSource("https://s3-us-west-2.amazonaws.com/link/to/file")
                    .setContent("<php?\nphpinfo()\n?>")
                    .setMode("000644")
                    .setOwner("www-data")
                    .setGroup("www-data");

            Config install = new Config(CFNINIT_CONFIG_INSTALL)
                    .putFile(indexFile);

            CFNInit cfnInit = new CFNInit(CFNINIT_CONFIGSET)
                    .addConfig(CFNINIT_CONFIGSET, install);

            SecurityGroup webServerSecurityGroup = resource(SecurityGroup.class, "WebServerSecurityGroup")
                    .groupDescription("Enable ports 80 and 22")
                    .ingress(ingress -> ingress.cidrIp(cidrIp), "tcp", 80, 22);

            Instance webServerInstance = resource(Instance.class, "WebServerInstance")
                    .addCFNInit(cfnInit)
                    .imageId("ami-0def3275")
                    .instanceType("t2.micro")
                    .securityGroupIds(webServerSecurityGroup)
                    .keyName(keyNameVar);
        }
    }
}

