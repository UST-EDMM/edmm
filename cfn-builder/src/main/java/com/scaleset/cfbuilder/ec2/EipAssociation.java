package com.scaleset.cfbuilder.ec2;

import com.scaleset.cfbuilder.annotations.Type;
import com.scaleset.cfbuilder.core.Resource;

/**
 * Constructs an {@code EipAssociation} to associate an Elastic IP address with an Amazon EC2 {@link Instance}.
 * <br>
 * Type: {@code AWS::EC2::EIPAssociation}
 *
 * @see <a href="https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-properties-ec2-eip-association.html">Documentation
 * Reference</a>
 */
@Type("AWS::EC2::EIPAssociation")
public interface EipAssociation extends Resource {

    EipAssociation allocationId(Object value);

    EipAssociation eIP(Object value);

    EipAssociation instanceId(Object value);

    EipAssociation networkInterfaceId(Object value);

    EipAssociation privateIpAddress(Object value);
}
