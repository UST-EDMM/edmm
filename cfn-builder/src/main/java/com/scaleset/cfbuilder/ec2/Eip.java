package com.scaleset.cfbuilder.ec2;

import com.scaleset.cfbuilder.annotations.Type;
import com.scaleset.cfbuilder.core.Resource;

/**
 * Constructs an {@code Eip} to allocate an Elastic IP (EIP) address and optionally, associate it with an Amazon EC2
 * {@link Instance}.
 * <br>
 * Type: {@code AWS::EC2::EIP}
 *
 * @see <a href="https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-properties-ec2-eip.html">Documentation
 * Reference</a>
 */
@Type("AWS::EC2::EIP")
public interface Eip extends Resource {

    Eip instanceId(Object value);

    Eip domain(Object value);
}
