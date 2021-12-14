package io.github.edmm.plugins.cfn.api;

import java.util.Objects;

import io.github.edmm.core.plugin.Authenticator;
import io.github.edmm.core.transformation.InstanceTransformationException;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.cloudformation.AmazonCloudFormation;
import com.amazonaws.services.cloudformation.AmazonCloudFormationClientBuilder;
import com.amazonaws.services.cloudformation.model.AmazonCloudFormationException;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public class AuthenticatorImpl implements Authenticator {

    private final String region;
    private final ProfileCredentialsProvider credentialsProvider;
    private AmazonCloudFormation cloudFormation;

    public AuthenticatorImpl(String region, String profileName) {
        this.region = Objects.requireNonNull(region);
        if (StringUtils.isNotBlank(profileName)) {
            this.credentialsProvider = new ProfileCredentialsProvider(profileName);
        } else {
            this.credentialsProvider = new ProfileCredentialsProvider();
        }
    }

    @Override
    public void authenticate() {
        this.retrieveCredentials();
        this.createCloudFormation();
    }

    private void retrieveCredentials() {
        try {
            this.credentialsProvider.getCredentials();
        } catch (AmazonClientException e) {
            throw new InstanceTransformationException(
                "Failed to locate credentials for AWS! Make sure they are in ~/.aws/credentials");
        }
    }

    private void createCloudFormation() {
        try {
            this.cloudFormation = AmazonCloudFormationClientBuilder.standard()
                .withCredentials(credentialsProvider)
                .withRegion(region)
                .build();
        } catch (AmazonCloudFormationException e) {
            throw new InstanceTransformationException(
                "Failed to authenticate with AWS! Make sure that the credentials in ~/.aws/credentials are up to date and correct.");
        }
    }
}
