package io.github.edmm.plugins.kubernetes.api;

import java.io.FileReader;
import java.io.IOException;

import io.github.edmm.core.plugin.Authenticator;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.AppsV1Api;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.KubeConfig;
import lombok.Getter;

@Getter
public class AuthenticatorImpl implements Authenticator {

    private String kubeConfigPath;
    private String inputDeploymentName;

    private AppsV1Api appsApi;
    private CoreV1Api coreV1Api;
    private ApiClient client;

    public AuthenticatorImpl(String kubeConfigPath, String inputDeploymentName) {
        this.kubeConfigPath = kubeConfigPath;
        this.inputDeploymentName = inputDeploymentName;
    }

    @Override
    public void authenticate() {
        try {
            this.client = ClientBuilder.kubeconfig(KubeConfig.loadKubeConfig(new FileReader(this.kubeConfigPath))).build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // set the global default api-client to the in-cluster one from above
        Configuration.setDefaultApiClient(this.client);

        // the CoreV1Api loads default api-client from global configuration.
        this.appsApi = new AppsV1Api(Configuration.getDefaultApiClient());
        this.coreV1Api = new CoreV1Api(Configuration.getDefaultApiClient());
    }
}