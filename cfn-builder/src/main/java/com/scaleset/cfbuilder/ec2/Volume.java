package com.scaleset.cfbuilder.ec2;

import com.scaleset.cfbuilder.annotations.Type;
import com.scaleset.cfbuilder.core.Taggable;

/**
 * Constructs a {@code Volume} to create a new Amazon Elastic Block Store (Amazon EBS) volume.
 * <br>
 * Type: {@code AWS::EC2::Volume}
 *
 * @see <a href="https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-properties-ec2-ebs-volume.html">Documentation
 * Reference</a>
 */
@Type("AWS::EC2::Volume")
public interface Volume extends Taggable {
    Volume autoEnableIO(Boolean value);

    Volume availabilityZone(Object value);

    Volume encrypted(Boolean value);

    Volume iops(Integer value);

    Volume kmsKeyId(Object value);

    Volume size(Integer value);

    Volume snapshotId(Object value);

    Volume volumeType(Object value);

    default Volume name(String name) {
        tag("Name", name);
        return this;
    }
}
