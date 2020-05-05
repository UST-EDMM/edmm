package com.scaleset.cfbuilder.ec2;

import java.util.function.Consumer;

import com.scaleset.cfbuilder.annotations.Type;
import com.scaleset.cfbuilder.core.Resource;

/**
 * Constructs a {@code SecurityGroup} to create an an Amazon EC2 security group.
 * <br>
 * Type: {@code AWS::EC2::SecurityGroup}
 *
 * @see <a href="https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-properties-ec2-security-group.html">Documentation
 * Reference</href="https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-properties-ec2-security-group.html">
 */
@Type("AWS::EC2::SecurityGroup")
public interface SecurityGroup extends Resource {

    SecurityGroup groupDescription(String description);

    SecurityGroup vpcId(Object vpcId);

    SecurityGroup securityGroupIngress(AnonymousSecurityGroupIngress... securityGroupIngress);

    default SecurityGroup ingress(Consumer<AnonymousSecurityGroupIngress> callback, String protocol, Object... ports) {

        for (Object value : ports) {
            int from, to;
            if (value instanceof Number) {
                from = ((Number) value).intValue();
                to = from;
            } else if (value instanceof PortRange) {
                PortRange range = (PortRange) value;
                from = range.from;
                to = range.to;
            } else {
                throw new IllegalArgumentException("Not a valid port or port range");
            }
            AnonymousSecurityGroupIngress ingress = new AnonymousSecurityGroupIngress(from, to);
            ingress.ipProtocol(protocol);
            if (callback != null) {
                callback.accept(ingress);
            }
            securityGroupIngress(ingress);
        }
        return this;
    }

    public class PortRange {

        int from;
        int to;

        public PortRange(int from, int to) {
            this.from = from;
            this.to = to;
        }

        public static PortRange range(int from, int to) {
            return new PortRange(from, to);
        }
    }

    public class AnonymousSecurityGroupIngress {

        private String ipProtocol;
        private Object sourceSecurityGroupName;
        private Object sourceSecurityGroupId;
        private Object sourceSecurityGroupOwnerId;
        private Object cidrIp;
        private int fromPort;
        private int toPort;

        public AnonymousSecurityGroupIngress() {
        }

        public AnonymousSecurityGroupIngress(int fromPort, int toPort) {
            this.fromPort = fromPort;
            this.toPort = toPort;
        }

        public Object getCidrIp() {
            return cidrIp;
        }

        public int getFromPort() {
            return fromPort;
        }

        public String getIpProtocol() {
            return ipProtocol;
        }

        public Object getSourceSecurityGroupId() {
            return sourceSecurityGroupId;
        }

        public Object getSourceSecurityGroupName() {
            return sourceSecurityGroupName;
        }

        public Object getSourceSecurityGroupOwnerId() {
            return sourceSecurityGroupOwnerId;
        }

        public int getToPort() {
            return toPort;
        }

        public AnonymousSecurityGroupIngress cidrIp(Object cidrIp) {
            this.cidrIp = cidrIp;
            return this;
        }

        public AnonymousSecurityGroupIngress ipProtocol(String ipProtocol) {
            this.ipProtocol = ipProtocol;
            return this;
        }

        public AnonymousSecurityGroupIngress sourceSecurityGroupId(Object sourceSecurityGroupId) {
            this.sourceSecurityGroupId = sourceSecurityGroupId;
            return this;
        }

        public AnonymousSecurityGroupIngress sourceSecurityGroupName(Object sourceSecurityGroupName) {
            this.sourceSecurityGroupName = sourceSecurityGroupName;
            return this;
        }

        public AnonymousSecurityGroupIngress sourceSecurityGroupOwnerId(Object sourceSecurityGroupOwnerId) {
            this.sourceSecurityGroupOwnerId = sourceSecurityGroupOwnerId;
            return this;
        }
    }
}
