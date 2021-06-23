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

    private final String kubeConfigPath;

    private AppsV1Api appsApi;
    private CoreV1Api coreV1Api;
    private ApiClient client;

    public AuthenticatorImpl(String kubeConfigPath) {
        this.kubeConfigPath = kubeConfigPath;
    }

    @Override
    public void authenticate() {
        try {
            this.client = ClientBuilder.kubeconfig(KubeConfig.loadKubeConfig(new FileReader(this.kubeConfigPath))).build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Configuration.setDefaultApiClient(this.client);

        this.appsApi = new AppsV1Api(Configuration.getDefaultApiClient());
        this.coreV1Api = new CoreV1Api(Configuration.getDefaultApiClient());
    }
}
