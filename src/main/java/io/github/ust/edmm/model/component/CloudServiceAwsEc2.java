package io.github.ust.edmm.model.component;

import java.util.Optional;

import io.github.ust.edmm.core.parser.MappingEntity;
import io.github.ust.edmm.model.support.Attribute;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = true)
public class CloudServiceAwsEc2 extends CloudServiceAws {

    public static Attribute<String> VPC_SECURITY_GROUP = new Attribute<>("vpc_security_group", String.class);
    public static Attribute<String> SUBNET = new Attribute<>("subnet", String.class);

    public CloudServiceAwsEc2(MappingEntity mappingEntity) {
        super(mappingEntity);
    }

    public Optional<String> getVpcSecurityGroup() {
        return getProperty(VPC_SECURITY_GROUP);
    }

    public Optional<String> getSubnet() {
        return getProperty(SUBNET);
    }
}
