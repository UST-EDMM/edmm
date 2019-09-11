package com.scaleset.cfbuilder.ec2;

import com.scaleset.cfbuilder.annotations.Type;
import com.scaleset.cfbuilder.cloudformation.Authentication;
import com.scaleset.cfbuilder.core.Taggable;
import com.scaleset.cfbuilder.ec2.instance.CreditSpecification;
import com.scaleset.cfbuilder.ec2.instance.EC2BlockDeviceMapping;
import com.scaleset.cfbuilder.ec2.instance.EC2MountPoint;
import com.scaleset.cfbuilder.ec2.instance.EC2NetworkInterface;
import com.scaleset.cfbuilder.ec2.instance.ElasticGpuSpecification;
import com.scaleset.cfbuilder.ec2.instance.SSMAssociation;
import com.scaleset.cfbuilder.ec2.metadata.CFNInit;
import com.scaleset.cfbuilder.ec2.networkinterface.Ipv6Address;

/**
 * Constructs an EC2 {@code Instance}.
 * <br>
 * Type: {@code AWS::EC2::Instance}
 *
 * @see <a href="https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-properties-ec2-instance.html">Documentation
 * Reference</a>
 */
@Type("AWS::EC2::Instance")
public interface Instance extends Taggable {

    Instance affinity(Object value);

    Instance availabilityZone(Object value);

    Instance blockDeviceMappings(EC2BlockDeviceMapping... values);

    Instance creditSpecification(CreditSpecification value);

    Instance disableApiTermination(Boolean value);

    Instance ebsOptimized(Boolean value);

    Instance elasticGpuSpecifications(ElasticGpuSpecification... values);

    Instance hostId(Object value);

    Instance iamInstanceProfile(Object value);

    Instance imageId(Object value);

    Instance instanceInitiatedShutdownBehavior(Object value);

    Instance instanceType(Object value);

    Instance ipv6AddressCount(Integer value);

    Instance ipv6Addresses(Ipv6Address... values);

    Instance kernelId(Object value);

    Instance keyName(Object value);

    Instance monitoring(Boolean value);

    Instance networkInterfaces(EC2NetworkInterface... values);

    Instance placementGroupName(Object value);

    Instance privateIpAddress(Object value);

    Instance ramdiskId(Object value);

    Instance securityGroupIds(Object... values);

    Instance securityGroups(Object... values);

    Instance sourceDestCheck(Boolean value);

    Instance ssmAssociations(SSMAssociation... values);

    Instance subnetId(Object subnetId);

    Instance tenancy(Object value);

    Instance userData(UserData userData);

    Instance volumes(EC2MountPoint... values);

    Instance additionalInfo(Object value);

    default Instance name(String name) {
        tag("Name", name);
        return this;
    }

    // Non property additions
    Instance addCFNInit(CFNInit cfnInit);

    Instance authentication(Authentication authentication);
}
