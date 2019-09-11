package com.scaleset.cfbuilder.ec2;

import com.scaleset.cfbuilder.annotations.Type;
import com.scaleset.cfbuilder.core.Taggable;
import com.scaleset.cfbuilder.ec2.networkinterface.Ipv6Address;
import com.scaleset.cfbuilder.ec2.networkinterface.PrivateIpAddressSpecification;

/**
 * Describes a {@code NetworkInterface} in an EC2 instance. Provided in a list in the NetworkInterfaces property of EC2
 * {@link Instance}.
 * <br>
 * Type: {@code AWS::EC2::NetworkInterface}
 *
 * @see <a href="https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-resource-ec2-network-interface.html">Documentation
 * Reference</a>
 */
@Type("AWS::EC2::NetworkInterface")
public interface NetworkInterface extends Taggable {

    NetworkInterface description(Object value);

    NetworkInterface groupSet(Object... values);

    NetworkInterface ipv6AddressCount(Integer value);

    NetworkInterface ipv6Addresses(Ipv6Address... values);

    NetworkInterface privateIpAddress(Object value);

    NetworkInterface privateIpAddresses(PrivateIpAddressSpecification... values);

    NetworkInterface secondaryPrivateIpAddressCount(Integer value);

    NetworkInterface sourceDestCheck(Boolean value);

    NetworkInterface subnetId(Object value);

    default NetworkInterface name(String name) {
        tag("Name", name);
        return this;
    }
}
