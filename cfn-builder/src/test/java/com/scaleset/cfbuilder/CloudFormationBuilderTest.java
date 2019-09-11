package com.scaleset.cfbuilder;

import com.scaleset.cfbuilder.core.Module;
import com.scaleset.cfbuilder.core.Parameter;
import com.scaleset.cfbuilder.core.Template;
import com.scaleset.cfbuilder.ec2.Instance;
import com.scaleset.cfbuilder.ec2.SecurityGroup;
import com.scaleset.cfbuilder.rds.DBInstance;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class CloudFormationBuilderTest extends Module {

    @Test
    public void testTemplateBuilding() throws Exception {
        Template lampTemplate = new Template();

        new CloudFormationBuilderTest.TestModule().id("").template(lampTemplate).build();

        assertNotNull(lampTemplate);

        System.err.println(lampTemplate.toString(true));
    }

    class TestModule extends Module {
        private static final String KEYNAME_DESCRIPTION = "Name of an existing EC2 KeyPair to enable SSH access to " +
                "the instances";
        private static final String KEYNAME_TYPE = "AWS::EC2::KeyPair::KeyName";
        private static final String KEYNAME_CONSTRAINT_DESCRIPTION = "must be the name of an existing EC2 KeyPair.";

        public void build() {

            Parameter keyName = (Parameter) option("KeyName").orElseGet(
                    () -> strParam("KeyName")
                            .type(KEYNAME_TYPE)
                            .description(KEYNAME_DESCRIPTION)
                            .constraintDescription(KEYNAME_CONSTRAINT_DESCRIPTION));

            Object cidrIp = "0.0.0.0/0";
            Object keyNameVar = template.ref("KeyName");
            Object webServerSecurityGroupName = template.ref("WebServerSecurityGroup");
            Object dbEc2SecurityGroupId = template.fnGetAtt("DBEC2SecurityGroup", "GroupId");

            SecurityGroup webServerSecurityGroup = resource(SecurityGroup.class, "WebServerSecurityGroup")
                    .groupDescription("Enable ports 80 and 22")
                    .ingress(ingress -> ingress.cidrIp(cidrIp), "tcp", 80, 22);
            SecurityGroup dbEc2SecurityGroup = resource(SecurityGroup.class, "DBEC2SecurityGroup")
                    .groupDescription("Open database for access")
                    .ingress(ingress -> ingress.sourceSecurityGroupName(webServerSecurityGroupName), "tcp", 3306);

            Instance webServerInstance = resource(Instance.class, "WebServerInstance")
                    .imageId("ami-0def3275")
                    .instanceType("t2.micro")
                    .securityGroupIds(webServerSecurityGroup)
                    .keyName(keyNameVar);

            resource(DBInstance.class, "MySQLDatabase")
                    .engine("MySQL")
                    .dBName("mydatabase")
                    .masterUsername("root")
                    .masterUserPassword("abcd1234")
                    .dBInstanceClass("db.t2.micro")
                    .allocatedStorage(20)
                    .storageType("gp2")
                    .vPCSecurityGroups(dbEc2SecurityGroupId);

            Object publicDNSName = webServerInstance.fnGetAtt("PublicDnsName");

            output("websiteURL", publicDNSName, "URL for newly created LAMP stack");
        }
    }
}
