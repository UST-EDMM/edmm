package io.github.ust.edmm.model.component;

import java.util.Optional;

import io.github.ust.edmm.core.parser.MappingEntity;
import io.github.ust.edmm.model.support.Attribute;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = true)
public class CloudServiceAws extends RootComponent {

    public static Attribute<String> AWS_ACCESS_KEY = new Attribute<>("aws_access_key", String.class);
    public static Attribute<String> AWS_SECRET_ACCESS_KEY = new Attribute<>("aws_secret_access_key", String.class);
    public static Attribute<String> AWS_REGION = new Attribute<>("aws_region", String.class);

    public CloudServiceAws(MappingEntity mappingEntity) {
        super(mappingEntity);
    }

    public Optional<String> getAwsAccessKey() {
        return getProperty(AWS_ACCESS_KEY);
    }

    public Optional<String> getAwsSecretAccessKey() {
        return getProperty(AWS_SECRET_ACCESS_KEY);
    }

    public Optional<String> getAwsRegion() {
        return getProperty(AWS_REGION);
    }
}
