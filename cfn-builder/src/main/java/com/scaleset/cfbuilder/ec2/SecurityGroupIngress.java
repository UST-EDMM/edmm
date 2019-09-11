package com.scaleset.cfbuilder.ec2;

import com.scaleset.cfbuilder.annotations.Type;
import com.scaleset.cfbuilder.core.Resource;

/**
 * Constructs a {@code SecurityGroupIngress} to add an ingress rule to an Amazon EC2 or Amazon VPC security group.
 * <br>
 * Type: {@code AWS::EC2::SecurityGroupIngress}
 *
 * @see <a href="https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-properties-ec2-security-group-ingress.html">Documentation
 * Reference</a>
 */
@Type("AWS::EC2::SecurityGroupIngress")
public interface SecurityGroupIngress extends Resource {

    SecurityGroupIngress cidrIp(Object cidrIp);

    SecurityGroupIngress fromPort(int fromPort);

    SecurityGroupIngress groupId(Object groupId);

    SecurityGroupIngress groupName(Object groupName);

    SecurityGroupIngress ipProtocol(String ipProtocol);

    default SecurityGroupIngress port(int port) {
        fromPort(port);
        toPort(port);
        return this;
    }

    default SecurityGroupIngress range(int from, int to) {
        fromPort(from);
        toPort(to);
        return this;
    }

    SecurityGroupIngress sourceSecurityGroupId(Object sourceSecurityGroupId);

    SecurityGroupIngress sourceSecurityGroupName(Object sourceSecurityGroupName);

    SecurityGroupIngress sourceSecurityGroupOwnerId(Object sourceSecurityGroupOwnerId);

    SecurityGroupIngress toPort(int toPort);
}
