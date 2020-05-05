package io.github.edmm.plugins.cfn.api;

import io.github.edmm.core.plugin.Authenticator;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudformation.AmazonCloudFormation;
import com.amazonaws.services.cloudformation.AmazonCloudFormationClientBuilder;
import lombok.Getter;

@Getter
public class AuthenticatorImpl implements Authenticator {

    private final ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();
    private AmazonCloudFormation cloudFormation;

    @Override
    public void authenticate() {
        try {
            credentialsProvider.getCredentials();
        } catch (AmazonClientException e) {
            System.out.println("Failed to locate credentials for AWS! Make sure they are in ~/.aws/credentials");
        }
        this.cloudFormation = AmazonCloudFormationClientBuilder.standard()
            .withCredentials(credentialsProvider)
            .withRegion(Regions.US_EAST_1)
            .build();
    }
}
