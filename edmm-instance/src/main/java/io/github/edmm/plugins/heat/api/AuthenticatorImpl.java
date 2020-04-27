package io.github.edmm.plugins.heat.api;

import io.github.edmm.core.plugin.Authenticator;
import lombok.Getter;
import org.openstack4j.api.OSClient;
import org.openstack4j.model.common.Identifier;
import org.openstack4j.openstack.OSFactory;

@Getter
public class AuthenticatorImpl implements Authenticator {

    private OSClient.OSClientV3 heatClient;
    private final String endpoint;
    private final String userName;
    private final String password;
    private final String domainName;
    private final String projectId;

    public AuthenticatorImpl(String endpoint, String userName, String password, String domainName, String projectId) {
        this.endpoint = endpoint;
        this.userName = userName;
        this.password = password;
        this.domainName = domainName;
        this.projectId = projectId;
    }

    @Override
    public void authenticate() {
        this.heatClient = OSFactory.builderV3()
            .endpoint(this.endpoint)
            .credentials(this.userName, this.password, Identifier.byName(this.domainName))
            .scopeToProject(Identifier.byId(this.projectId))
            .authenticate();
    }
}
