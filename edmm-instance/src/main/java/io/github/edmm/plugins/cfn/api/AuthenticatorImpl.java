package io.github.edmm.plugins.cfn.api;

import io.github.edmm.core.plugin.Authenticator;
import io.github.edmm.core.transformation.InstanceTransformationException;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudformation.AmazonCloudFormation;
import com.amazonaws.services.cloudformation.AmazonCloudFormationClientBuilder;
import com.amazonaws.services.cloudformation.model.AmazonCloudFormationException;
import lombok.Getter;

@Getter
public class AuthenticatorImpl implements Authenticator {

    private final ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();
    private AmazonCloudFormation cloudFormation;

    @Override
    public void authenticate() {
        this.retrieveCredentials();
        this.createCloudFormation();
    }

    private void retrieveCredentials() {
        try {
            this.credentialsProvider.getCredentials();
        } catch (AmazonClientException e) {
            throw new InstanceTransformationException("Failed to locate credentials for AWS! Make sure they are in ~/.aws/credentials");
        }
    }

    private void createCloudFormation() {
        try {
            this.cloudFormation = AmazonCloudFormationClientBuilder.standard()
                .withCredentials(credentialsProvider)
                .withRegion(Regions.US_EAST_1)
                .build();
        } catch (AmazonCloudFormationException e) {
            throw new InstanceTransformationException("Failed to authenticate with AWS! Make sure that the credentials in ~/.aws/credentials are up to date and correct.");
        }
    }
}
