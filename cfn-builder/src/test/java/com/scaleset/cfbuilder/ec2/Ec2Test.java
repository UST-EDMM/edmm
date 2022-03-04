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
    private String expectedVpcInstanceWithEniTemplateString = """
        ---
        AWSTemplateFormatVersion: "2010-09-09"
        Resources:
          ControlPortAddress:
            Type: "AWS::EC2::EIP"
            Properties:
              Domain: "vpc"
          Ec2Instance:
            Type: "AWS::EC2::Instance"
            Properties:
              ImageId:
                Fn::FindInMap:
                - "RegionMap"
                - Ref: "'AWS::Region'"
                - "AMI"
              KeyName:
                Ref: "KeyName"
              NetworkInterfaces: []
              UserData:
                Fn::Base64:
                  Fn::Join:
                  - ""
                  - - "#!/bin/bash -xe"
                    - "yum install ec2-net-utils -y"
                    - "ec2ifup eth1"
                    - "service httpd start"
              Tags:
              - Value: "Role"
                Key: "Key"
              - Value: "Test Instance"
                Key: "Value"
          AssociateControlPort:
            Type: "AWS::EC2::EIPAssociation"
            Properties:
              AllocationId:
                Fn::GetAtt:
                - "ControlPortAddress"
                - "AllocationId"
              NetworkInterfaceId:
                Ref: "controlXface"
          controlXface:
            Type: "AWS::EC2::NetworkInterface"
            Properties:
              SubnetId:
                Ref: "SubnetId"
              Description: "Interace for controlling traffic such as SSH"
              GroupSet:
              - Ref: "SSHSecurityGroup"
              SourceDestCheck: true
              Tags:
              - Value: "Network"
                Key: "Key"
              - Value: "Control"
                Key: "Value"
          SSHSecurityGroup:
            Type: "AWS::EC2::SecurityGroup"
            Properties:
              VpcId:
                Ref: "VpcId"
              GroupDescription: "Enable SSH access via port 22"
              SecurityGroupIngress:
              - IpProtocol: "tcp"
                CidrIp: "0.0.0.0/0"
                FromPort: 22
                ToPort: 22
              - IpProtocol: "tcp"
                CidrIp: "0.0.0.0/0"
                FromPort: 22
                ToPort: 22
          webXface:
            Type: "AWS::EC2::NetworkInterface"
            Properties:
              SubnetId:
                Ref: "SubNetId"
              Description: "Interface for controlling traffic such as SSH"
              GroupSet:
              - Ref: "WebSecurityGroup"
              SourceDestCheck: true
              Tags:
              - Value: "Network"
                Key: "Key"
              - Value: "Web"
                Key: "Value"
          WebSecurityGroup:
            Type: "AWS::EC2::SecurityGroup"
            Properties:
              VpcId:
                Ref: "VpcId"
              GroupDescription: "Enable HTTP access via user defined port"
              SecurityGroupIngress:
              - IpProtocol: "tcp"
                CidrIp: "0.0.0.0/0"
                FromPort: 80
                ToPort: 80
              - IpProtocol: "tcp"
                CidrIp: "0.0.0.0/0"
                FromPort: 80
                ToPort: 80
          WebPortAddress:
            Type: "AWS::EC2::EIP"
            Properties:
              Domain: "vpc"
          AssociateWebPort:
            Type: "AWS::EC2::EIPAssociation"
            Properties:
              AllocationId:
                Fn::GetAtt:
                - "WebPortAddress"
                - "AllocationId"
              NetworkInterfaceId:
                Ref: "webXface"
        """;

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
