package com.scaleset.cfbuilder.ec2;

import com.scaleset.cfbuilder.core.Fn;
import com.scaleset.cfbuilder.core.Module;
import com.scaleset.cfbuilder.core.Template;
import com.scaleset.cfbuilder.ec2.instance.CreditSpecification;
import com.scaleset.cfbuilder.ec2.instance.EC2BlockDeviceMapping;
import com.scaleset.cfbuilder.ec2.instance.EC2MountPoint;
import com.scaleset.cfbuilder.ec2.instance.EC2NetworkInterface;
import com.scaleset.cfbuilder.ec2.instance.ElasticGpuSpecification;
import com.scaleset.cfbuilder.ec2.instance.SSMAssociation;
import com.scaleset.cfbuilder.ec2.instance.ec2blockdevicemapping.EC2EBSBlockDevice;
import com.scaleset.cfbuilder.ec2.instance.ssmassociation.AssociationParameter;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test ec2 instance templates built with the cloudformation builder. Ec2withEbs and autoPubIP examples taken from
 * <a href="https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-properties-ec2-instance.html">here</a>.
 */
public class InstanceTest {
    private String expectedEc2withEbsTemplateString = "---\n" +
            "AWSTemplateFormatVersion: \"2010-09-09\"\n" +
            "Description: \"Ec2 block device mapping\"\n" +
            "Resources:\n" +
            "  MyEC2Instance:\n" +
            "    Type: \"AWS::EC2::Instance\"\n" +
            "    Properties:\n" +
            "      ImageId: \"ami-79fd7eee\"\n" +
            "      KeyName: \"testkey\"\n" +
            "      BlockDeviceMappings:\n" +
            "      - DeviceName: \"/dev/sdm\"\n" +
            "        Ebs:\n" +
            "          DeleteOnTermination: false\n" +
            "          Iops: 200\n" +
            "          VolumeSize: \"20\"\n" +
            "          VolumeType: \"io1\"\n" +
            "      - DeviceName: \"/dev/sdk\"\n" +
            "        NoDevice: false\n";

    private String expectedAutoPubIPTemplateString = "---\n" +
            "AWSTemplateFormatVersion: \"2010-09-09\"\n" +
            "Resources:\n" +
            "  Ec2Instance:\n" +
            "    Type: \"AWS::EC2::Instance\"\n" +
            "    Properties:\n" +
            "      ImageId:\n" +
            "        Fn::FindInMap:\n" +
            "        - \"RegionMap\"\n" +
            "        - Ref: \"AWS::Region\"\n" +
            "        - \"AMI\"\n" +
            "      KeyName:\n" +
            "        Ref: \"KeyName\"\n" +
            "      NetworkInterfaces:\n" +
            "      - AssociatePublicIpAddress: true\n" +
            "        DeviceIndex: \"0\"\n" +
            "        GroupSet:\n" +
            "        - Ref: \"myVPCEC2SecurityGroup\"\n" +
            "        SubnetId:\n" +
            "          Ref: \"PublicSubnet\"\n";

    @Test
    public void ec2withEbs() {
        Template ec2withEbsTemplate = new Template();
        new Ec2withEbsModule().id("").template(ec2withEbsTemplate).build();
        String ec2withEbsTemplateString = ec2withEbsTemplate.toString(true);

        assertNotNull(ec2withEbsTemplate);
        assertEquals(expectedEc2withEbsTemplateString, ec2withEbsTemplateString);
        System.err.println(ec2withEbsTemplateString);
    }

    @Test
    public void autoPubIP() {
        Template autoPubIPTemplate = new Template();
        new AutoPubIPModule().id("").template(autoPubIPTemplate).build();
        String autoPubIPTemplateString = autoPubIPTemplate.toString(true);

        assertNotNull(autoPubIPTemplate);
        assertEquals(expectedAutoPubIPTemplateString, autoPubIPTemplateString);
        System.err.println(autoPubIPTemplateString);
    }

    @Test
    public void testProperties() {
        Template testPropertiesTemplate = new Template();
        new TestPropertiesModule().id("").template(testPropertiesTemplate).build();
        String autoPubIPTemplateString = testPropertiesTemplate.toString(true);

        assertNotNull(testPropertiesTemplate);
        System.err.println(autoPubIPTemplateString);
    }

    class Ec2withEbsModule extends Module {
        public void build() {
            this.template.setDescription("Ec2 block device mapping");

            EC2EBSBlockDevice ec2EBSBlockDeviceA = new EC2EBSBlockDevice()
                    .volumeType("io1")
                    .iops(200)
                    .deleteOnTermination(false)
                    .volumeSize("20");
            EC2BlockDeviceMapping ec2BlockDeviceMappingA = new EC2BlockDeviceMapping()
                    .deviceName("/dev/sdm")
                    .ebs(ec2EBSBlockDeviceA);

            EC2BlockDeviceMapping ec2BlockDeviceMappingB = new EC2BlockDeviceMapping()
                    .deviceName("/dev/sdk")
                    .noDevice(false);

            resource(Instance.class, "MyEC2Instance")
                    .imageId("ami-79fd7eee")
                    .keyName("testkey")
                    .blockDeviceMappings(ec2BlockDeviceMappingA, ec2BlockDeviceMappingB);
        }
    }

    class AutoPubIPModule extends Module {
        public void build() {
            EC2NetworkInterface ec2NetworkInterface = new EC2NetworkInterface()
                    .associatePublicIpAddress(true)
                    .deviceIndex("0")
                    .addGroupSet(ref("myVPCEC2SecurityGroup"))
                    .subnetId(ref("PublicSubnet"));

            resource(Instance.class, "Ec2Instance")
                    .imageId(new Fn("FindInMap",
                            "RegionMap",
                            ref("AWS::Region"),
                            "AMI"))
                    .keyName(ref("KeyName"))
                    .networkInterfaces(ec2NetworkInterface);
        }
    }

    class TestPropertiesModule extends Module {
        public void build() {
            CreditSpecification creditSpecification = new CreditSpecification().cPUCredits("100");
            creditSpecification.setcPUCredits(creditSpecification.getcPUCredits());
            ElasticGpuSpecification elasticGpuSpecification = new ElasticGpuSpecification().type("abc");
            elasticGpuSpecification.setType(elasticGpuSpecification.getType());
            EC2EBSBlockDevice ec2EBSBlockDevice = new EC2EBSBlockDevice()
                    .deleteOnTermination(false)
                    .iops(199)
                    .volumeSize("volumeSizeVal")
                    .volumeType("volumeTypeVal")
                    .encrypted(true)
                    .snapshotId("snapshotIdVal");
            EC2BlockDeviceMapping ec2BlockDeviceMapping = new EC2BlockDeviceMapping()
                    .deviceName("deviceNameVal")
                    .noDevice(false)
                    .ebs(ec2EBSBlockDevice)
                    .virtualName("virtualNameVal");
            EC2MountPoint ec2MountPoint = new EC2MountPoint()
                    .device("deviceVal")
                    .volumeId("volumeIdVal");
            AssociationParameter associationParameter = new AssociationParameter()
                    .addValue("valueVal")
                    .key("keyVal");
            SSMAssociation ssmAssociation = new SSMAssociation()
                    .documentName("documentNameVal")
                    .addAssociationParameters(associationParameter);
            resource(SecurityGroupIngress.class, "SecurityGroupIngressName")
                    .cidrIp("cidrIpVal")
                    .groupName("grouNameVal")
                    .sourceSecurityGroupName("sourceSecurityGroupNameVal")
                    .sourceSecurityGroupOwnerId("sourceSecurityGroupOwnerIdVal");
            resource(Instance.class, "Ec2Test")
                    .creditSpecification(creditSpecification)
                    .elasticGpuSpecifications(elasticGpuSpecification)
                    .blockDeviceMappings(ec2BlockDeviceMapping)
                    .volumes(ec2MountPoint)
                    .ssmAssociations(ssmAssociation);
        }
    }
}