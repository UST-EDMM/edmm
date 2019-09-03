package io.github.edmm.plugins.cfn;

import java.util.UUID;

public abstract class CloudFormationUtils {

    public static String getRandomBucketName() {
        return "edmm-bucket-" + UUID.randomUUID();
    }

    public static String getRandomStackName() {
        return "edmm-stack-" + UUID.randomUUID();
    }

    public static String normalize(String input) {
        return input.replaceAll("[^A-Za-z0-9]", "");
    }
}
