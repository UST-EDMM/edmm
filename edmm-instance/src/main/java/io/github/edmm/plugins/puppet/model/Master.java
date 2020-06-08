package io.github.edmm.plugins.puppet.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import io.github.edmm.core.transformation.InstanceTransformationException;
import io.github.edmm.model.edimm.ComponentInstance;
import io.github.edmm.plugins.puppet.util.MasterInitializer;
import io.github.edmm.plugins.puppet.util.MasterNodeHandler;
import io.github.edmm.plugins.puppet.util.MasterSSHConfigurator;
import io.github.edmm.plugins.puppet.util.PuppetPropertiesHandler;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Master {
    private String id;
    private String hostName;
    private String user;
    private String ip;
    private String privateKeyLocation;
    private Integer sshPort;
    private Session session;
    private String operatingSystem;
    private String operatingSystemRelease;
    private List<Node> nodes;
    private String puppetVersion;
    private String createdAtTimestamp;
    private String generatedPrivateKey;
    private String generatedPublicKey;
    // master is reachable, thus is running
    private PuppetState.MasterStateAsComponentInstance state = PuppetState.MasterStateAsComponentInstance.running;

    public Master(String user, String ip, String privateKeyLocation, Integer sshPort) {
        this.user = user;
        this.ip = ip;
        this.privateKeyLocation = privateKeyLocation;
        this.sshPort = sshPort;
    }

    public void connectAndSetupMaster() {
        this.createJschSession();
        this.initMasterData();
    }

    private void createJschSession() {
        try {
            JSch jsch = new JSch();
            jsch.addIdentity(this.privateKeyLocation);
            this.session = jsch.getSession(this.user, this.ip, this.sshPort);
            this.session.setConfig("StrictHostKeyChecking", "no");
            this.session.connect();
        } catch (JSchException e) {
            throw new InstanceTransformationException("Failed to connect with Puppet Master. Please make sure the correct user, host, sshPort and private key location of the Puppet Master is set.");
        }
    }

    private void initMasterData() {
        this.setupMaster();
        this.setupSSH();
        this.setupNodes();
    }

    private void setupMaster() {
        MasterInitializer masterInitializer = new MasterInitializer(this);
        masterInitializer.setupMaster();
    }

    private void setupSSH() {
        MasterSSHConfigurator masterSSHConfigurator = new MasterSSHConfigurator(this);
        masterSSHConfigurator.configurePuppetMaster();
    }

    private void setupNodes() {
        MasterNodeHandler masterNodeHandler = new MasterNodeHandler(this);
        masterNodeHandler.handleNodes();
    }

    private ChannelExec setupChannelExec() throws JSchException {
        return (ChannelExec) this.session.openChannel("exec");
    }

    public String executeCommandAndHandleResult(String command) {
        try {
            ChannelExec channelExec = this.setupChannelExec();
            BufferedReader reader = new BufferedReader(new InputStreamReader(channelExec.getInputStream()));

            channelExec.setCommand(command);
            channelExec.connect();

            return reader.readLine();
        } catch (JSchException | IOException e) {
            throw new InstanceTransformationException("Failed to query data from Puppet Master. Please make sure that PuppetDB on Puppet Master is up and running.");
        }
    }

    public ComponentInstance toComponentInstance() {
        ComponentInstance componentInstance = new ComponentInstance();
        componentInstance.setId(this.id);
        componentInstance.setName(this.hostName);
        componentInstance.setCreatedAt(this.createdAtTimestamp);
        componentInstance.setType(this.operatingSystem + this.operatingSystemRelease);
        componentInstance.setState(this.state.toEDIMMComponentInstanceState());
        componentInstance.setInstanceProperties(PuppetPropertiesHandler.getComponentInstanceProperties(this.hostName, this.user, this.ip, this.privateKeyLocation, this.sshPort));

        return componentInstance;
    }
}
