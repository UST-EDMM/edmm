package com.scaleset.cfbuilder.ec2;

import com.scaleset.cfbuilder.annotations.Type;
import com.scaleset.cfbuilder.core.Resource;

/**
 * Constructs a {@code Subnet} to create a subnet in an existing VPC.
 * <br>
 * Type: {@code AWS::EC2::Subnet}
 *
 * @see <a href="https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-resource-ec2-subnet.html">Documentation
 * Reference</a>
 */
@Type("AWS::EC2::Subnet")
public interface Subnet extends Resource {

    Subnet availabilityZone(String value);

    Subnet cidrBlock(String value);

    Subnet vpcId(String value);
}
