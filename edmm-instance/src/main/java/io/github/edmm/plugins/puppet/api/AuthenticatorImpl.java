package io.github.edmm.plugins.puppet.api;

import io.github.edmm.core.plugin.Authenticator;
import io.github.edmm.core.transformation.InstanceTransformationException;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.Getter;

@Getter
public class AuthenticatorImpl implements Authenticator {

    private String puppetUser = "your-user-here";
    private String puppetMasterIP = "your-puppet-master-ip-here";
    private String puppetMasterPrivateKey = "your-private-key-location-here";
    private Integer puppetMasterPort = 22;
    private Session session;

    @Override
    public void authenticate() {
        try {
            JSch jsch = new JSch();
            jsch.addIdentity(puppetMasterPrivateKey);
            this.session = jsch.getSession(puppetUser, puppetMasterIP, puppetMasterPort);
            this.session.setConfig("StrictHostKeyChecking", "no");
            this.session.connect();
        } catch (JSchException e) {
            throw new InstanceTransformationException("Failed to connect with Puppet Master. Please make sure the correct user, host, port and private key location of the Puppet Master is set.");
        }
    }
}
