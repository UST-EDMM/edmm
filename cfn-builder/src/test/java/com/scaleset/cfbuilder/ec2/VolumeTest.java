package com.scaleset.cfbuilder.ec2;

import com.scaleset.cfbuilder.core.Fn;
import com.scaleset.cfbuilder.core.Module;
import com.scaleset.cfbuilder.core.Tag;
import com.scaleset.cfbuilder.core.Template;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test ec2 volumes templates built with the cloudformation builder. Examples taken from
 * <a href="https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-properties-ec2-ebs-volume.html">here</a>.
 */
public class VolumeTest {
    private String expectedEncEbsSnapTemplateString = "---\n" +
            "AWSTemplateFormatVersion: \"2010-09-09\"\n" +
            "Resources:\n" +
            "  NewVolume:\n" +
            "    Type: \"AWS::EC2::Volume\"\n" +
            "    Properties:\n" +
            "      Size: 100\n" +
            "      Encrypted: true\n" +
            "      AvailabilityZone:\n" +
            "        Fn::GetAtt:\n" +
            "        - \"Ec2Instance\"\n" +
            "        - \"AvailabilityZone\"\n" +
            "      Tags:\n" +
            "      - Value: \"MyTag\"\n" +
            "        Key: \"Key\"\n" +
            "      - Value: \"TagValue\"\n" +
            "        Key: \"Value\"\n";

    private String expectedEbs100IopsTemplateString = "---\n" +
            "AWSTemplateFormatVersion: \"2010-09-09\"\n" +
            "Resources:\n" +
            "  NewVolume:\n" +
            "    Type: \"AWS::EC2::Volume\"\n" +
            "    Properties:\n" +
            "      Size: 100\n" +
            "      VolumeType: \"io1\"\n" +
            "      Iops: 100\n" +
            "      AvailabilityZone:\n" +
            "        Fn::GetAtt:\n" +
            "        - \"EC2Instance\"\n" +
            "        - \"AvailabilityZone\"\n";

    @Test
    public void encEbsSnap() {
        Template EncEbsSnapTemplate = new Template();
        new EncEbsSnapModule().id("").template(EncEbsSnapTemplate).build();
        String EncEbsSnapTemplateString = EncEbsSnapTemplate.toString(true);

        assertNotNull(EncEbsSnapTemplate);
        assertEquals(expectedEncEbsSnapTemplateString, EncEbsSnapTemplateString);
        // System.err.println(EncEbsSnapTemplateString);
    }

    @Test
    public void ebs100Iops() {
        Template Ebs100IopsTemplate = new Template();
        new Ebs100IopsModule().id("").template(Ebs100IopsTemplate).build();
        String Ebs100IopsTemplateString = Ebs100IopsTemplate.toString(true);

        assertNotNull(Ebs100IopsTemplate);
        assertEquals(expectedEbs100IopsTemplateString, Ebs100IopsTemplateString);
        // System.err.println(Ebs100IopsTemplateString);
    }

    @Test
    public void volumeTest() {
        Template volumeTestTemplate = new Template();
        new VolumeTestModule().id("").template(volumeTestTemplate).build();
        String volumeTestTemplateString = volumeTestTemplate.toString(true);

        assertNotNull(volumeTestTemplate);
        // System.err.println(volumeTestTemplateString);
    }

    class EncEbsSnapModule extends Module {
        public void build() {
            resource(Volume.class, "NewVolume")
                .size(100)
                .encrypted(true)
                .availabilityZone(new Fn("GetAtt",
                    "Ec2Instance",
                    "AvailabilityZone"))
                .tags(new Tag("Key", "MyTag"),
                    new Tag("Value", "TagValue"));
        }
    }

    class Ebs100IopsModule extends Module {
        public void build() {
            resource(Volume.class, "NewVolume")
                .size(100)
                .volumeType("io1")
                .iops(100)
                .availabilityZone(new Fn("GetAtt",
                    "EC2Instance",
                    "AvailabilityZone"));
        }
    }

    class VolumeTestModule extends Module {
        public void build() {
            resource(Volume.class, "VolumeName")
                .autoEnableIO(true)
                .kmsKeyId("kmsKeyIdVal")
                .snapshotId("snapshotIdVal");
        }
    }
}
