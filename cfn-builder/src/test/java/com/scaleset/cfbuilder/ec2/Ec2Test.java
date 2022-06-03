package com.scaleset.cfbuilder.ec2;

import com.scaleset.cfbuilder.core.Fn;
import com.scaleset.cfbuilder.core.Module;
import com.scaleset.cfbuilder.core.Tag;
import com.scaleset.cfbuilder.core.Template;
import com.scaleset.cfbuilder.ec2.instance.EC2NetworkInterface;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test templates using all {@code AWS::EC2} types built with the cloudformation builder. Examples taken from <a
 * href="https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/quickref-ec2.html">here
 * </a>.
 */
public class Ec2Test {
    private String expectedVpcInstanceWithEniTemplateString = "---\n" +
            "AWSTemplateFormatVersion: \"2010-09-09\"\n" +
            "Resources:\n" +
            "  ControlPortAddress:\n" +
            "    Type: \"AWS::EC2::EIP\"\n" +
            "    Properties:\n" +
            "      Domain: \"vpc\"\n" +
            "  Ec2Instance:\n" +
            "    Type: \"AWS::EC2::Instance\"\n" +
            "    Properties:\n" +
            "      ImageId:\n" +
            "        Fn::FindInMap:\n" +
            "        - \"RegionMap\"\n" +
            "        - Ref: \"'AWS::Region'\"\n" +
            "        - \"AMI\"\n" +
            "      KeyName:\n" +
            "        Ref: \"KeyName\"\n" +
            "      NetworkInterfaces: []\n" +
            "      UserData:\n" +
            "        Fn::Base64:\n" +
            "          Fn::Join:\n" +
            "          - \"\"\n" +
            "          - - \"#!/bin/bash -xe\"\n" +
            "            - \"yum install ec2-net-utils -y\"\n" +
            "            - \"ec2ifup eth1\"\n" +
            "            - \"service httpd start\"\n" +
            "      Tags:\n" +
            "      - Value: \"Role\"\n" +
            "        Key: \"Key\"\n" +
            "      - Value: \"Test Instance\"\n" +
            "        Key: \"Value\"\n" +
            "  AssociateControlPort:\n" +
            "    Type: \"AWS::EC2::EIPAssociation\"\n" +
            "    Properties:\n" +
            "      AllocationId:\n" +
            "        Fn::GetAtt:\n" +
            "        - \"ControlPortAddress\"\n" +
            "        - \"AllocationId\"\n" +
            "      NetworkInterfaceId:\n" +
            "        Ref: \"controlXface\"\n" +
            "  controlXface:\n" +
            "    Type: \"AWS::EC2::NetworkInterface\"\n" +
            "    Properties:\n" +
            "      SubnetId:\n" +
            "        Ref: \"SubnetId\"\n" +
            "      Description: \"Interace for controlling traffic such as SSH\"\n" +
            "      GroupSet:\n" +
            "      - Ref: \"SSHSecurityGroup\"\n" +
            "      SourceDestCheck: true\n" +
            "      Tags:\n" +
            "      - Value: \"Network\"\n" +
            "        Key: \"Key\"\n" +
            "      - Value: \"Control\"\n" +
            "        Key: \"Value\"\n" +
            "  SSHSecurityGroup:\n" +
            "    Type: \"AWS::EC2::SecurityGroup\"\n" +
            "    Properties:\n" +
            "      VpcId:\n" +
            "        Ref: \"VpcId\"\n" +
            "      GroupDescription: \"Enable SSH access via port 22\"\n" +
            "      SecurityGroupIngress:\n" +
            "      - IpProtocol: \"tcp\"\n" +
            "        CidrIp: \"0.0.0.0/0\"\n" +
            "        FromPort: 22\n" +
            "        ToPort: 22\n" +
            "      - IpProtocol: \"tcp\"\n" +
            "        CidrIp: \"0.0.0.0/0\"\n" +
            "        FromPort: 22\n" +
            "        ToPort: 22\n" +
            "  webXface:\n" +
            "    Type: \"AWS::EC2::NetworkInterface\"\n" +
            "    Properties:\n" +
            "      SubnetId:\n" +
            "        Ref: \"SubNetId\"\n" +
            "      Description: \"Interface for controlling traffic such as SSH\"\n" +
            "      GroupSet:\n" +
            "      - Ref: \"WebSecurityGroup\"\n" +
            "      SourceDestCheck: true\n" +
            "      Tags:\n" +
            "      - Value: \"Network\"\n" +
            "        Key: \"Key\"\n" +
            "      - Value: \"Web\"\n" +
            "        Key: \"Value\"\n" +
            "  WebSecurityGroup:\n" +
            "    Type: \"AWS::EC2::SecurityGroup\"\n" +
            "    Properties:\n" +
            "      VpcId:\n" +
            "        Ref: \"VpcId\"\n" +
            "      GroupDescription: \"Enable HTTP access via user defined port\"\n" +
            "      SecurityGroupIngress:\n" +
            "      - IpProtocol: \"tcp\"\n" +
            "        CidrIp: \"0.0.0.0/0\"\n" +
            "        FromPort: 80\n" +
            "        ToPort: 80\n" +
            "      - IpProtocol: \"tcp\"\n" +
            "        CidrIp: \"0.0.0.0/0\"\n" +
            "        FromPort: 80\n" +
            "        ToPort: 80\n" +
            "  WebPortAddress:\n" +
            "    Type: \"AWS::EC2::EIP\"\n" +
            "    Properties:\n" +
            "      Domain: \"vpc\"\n" +
            "  AssociateWebPort:\n" +
            "    Type: \"AWS::EC2::EIPAssociation\"\n" +
            "    Properties:\n" +
            "      AllocationId:\n" +
            "        Fn::GetAtt:\n" +
            "        - \"WebPortAddress\"\n" +
            "        - \"AllocationId\"\n" +
            "      NetworkInterfaceId:\n" +
            "        Ref: \"webXface\"\n";

    @Test
    public void vpcInstanceWithEni() {
        Template vpcInstanceWithEniTemplate = new Template();
        new VpcInstanceWithEniModule().id("").template(vpcInstanceWithEniTemplate).build();
        String vpcInstanceWithEniTemplateString = vpcInstanceWithEniTemplate.toString(true);

        assertNotNull(vpcInstanceWithEniTemplate);
        assertEquals(expectedVpcInstanceWithEniTemplateString, vpcInstanceWithEniTemplateString);
        // System.err.println(vpcInstanceWithEniTemplateString);
    }

    class VpcInstanceWithEniModule extends Module {
        private String userDataString = "!Sub |\n" +
            "          #!/bin/bash -xe\n" +
            "          yum install ec2-net-utils -y\n" +
            "          ec2ifup eth1\n" +
            "          service httpd start";

        public void build() {
            resource(Eip.class, "ControlPortAddress")
                .domain("vpc");
            resource(EipAssociation.class, "AssociateControlPort")
                .allocationId(fnGetAtt("ControlPortAddress", "AllocationId"))
                .networkInterfaceId(ref("controlXface"));
            resource(Eip.class, "WebPortAddress")
                .domain("vpc");
            resource(EipAssociation.class, "AssociateWebPort")
                .allocationId(fnGetAtt("WebPortAddress", "AllocationId"))
                .networkInterfaceId(ref("webXface"));
            resource(SecurityGroup.class, "SSHSecurityGroup")
                .vpcId(ref("VpcId"))
                .groupDescription("Enable SSH access via port 22")
                .ingress(ingress -> ingress.cidrIp("0.0.0.0/0"), "tcp", 22, 22);
            resource(SecurityGroup.class, "WebSecurityGroup")
                .vpcId(ref("VpcId"))
                .groupDescription("Enable HTTP access via user defined port")
                .ingress(ingress -> ingress.cidrIp("0.0.0.0/0"), "tcp", 80, 80);
            resource(NetworkInterface.class, "controlXface")
                .subnetId(ref("SubnetId"))
                .description("Interace for controlling traffic such as SSH")
                .groupSet(ref("SSHSecurityGroup"))
                .sourceDestCheck(true)
                .tags(new Tag("Key", "Network"), new Tag("Value", "Control"));
            resource(NetworkInterface.class, "webXface")
                .subnetId(ref("SubNetId"))
                .description("Interface for controlling traffic such as SSH")
                .groupSet(ref("WebSecurityGroup"))
                .sourceDestCheck(true)
                .tags(new Tag("Key", "Network"), new Tag("Value", "Web"));
            EC2NetworkInterface networkInterfaceA = new EC2NetworkInterface()
                .networkInterfaceId(ref("controlXface"))
                .deviceIndex("0");
            EC2NetworkInterface networkInterfaceB = new EC2NetworkInterface()
                .networkInterfaceId(ref("webXface"))
                .deviceIndex("1");
            resource(Instance.class, "Ec2Instance")
                .imageId(new Fn("FindInMap", "RegionMap", ref("'AWS::Region'"), "AMI"))
                .keyName(ref("KeyName"))
                .networkInterfaces()
                .userData(new UserData(Fn.fnDelimiter("Join", "",
                    "#!/bin/bash -xe",
                    "yum install ec2-net-utils -y",
                    "ec2ifup eth1",
                    "service httpd start")))
                .tags(new Tag("Key", "Role"), new Tag("Value", "Test Instance"));
        }
    }
}
