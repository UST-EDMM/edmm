package com.scaleset.cfbuilder.ec2;

import com.scaleset.cfbuilder.annotations.Type;
import com.scaleset.cfbuilder.core.Resource;

/**
 * Constructs a {@code Vpc} to create a Virtual Private Cloud (VPC).
 * <br>
 * Type: {@code AWS::EC2::VPC}
 *
 * @see <a href="https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-resource-ec2-vpc.html">Documentation
 * Reference</a>
 */
@Type("AWS::EC2::VPC")
public interface Vpc extends Resource {

    Vpc cidrBlock(String cidrBlock);

    Vpc enableDnsSupport(Boolean flag);

    Vpc enableDnsHostnames(Boolean flag);

    Vpc instanceTenancy(String instanceTenancy);
}
