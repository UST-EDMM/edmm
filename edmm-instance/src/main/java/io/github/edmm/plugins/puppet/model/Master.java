package io.github.edmm.plugins.puppet.model;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import io.github.edmm.core.transformation.InstanceTransformationException;
import io.github.edmm.plugins.puppet.util.MasterInitializer;
import io.github.edmm.plugins.puppet.util.NodesHandler;
import io.github.edmm.plugins.puppet.util.SSHConfigurator;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
@Setter
public class Master {

    private final static Logger logger = LoggerFactory.getLogger(Master.class);

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
            throw new InstanceTransformationException("Failed to connect with Puppet Master. Please make sure the " +
                "correct user, host, sshPort, and private key location of the Puppet Master is set.");
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
        SSHConfigurator SSHConfigurator = new SSHConfigurator(this);
        SSHConfigurator.configurePuppetMaster();
    }

    private void setupNodes() {
        NodesHandler nodesHandler = new NodesHandler(this);
        nodesHandler.handleNodes();
    }

    private ChannelExec setupChannelExec() throws JSchException {
        return (ChannelExec) this.session.openChannel("exec");
    }

    public String getPrivateKey() {
        try (FileInputStream fileInputStream = new FileInputStream(this.privateKeyLocation)) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            StringBuilder stringBuilder = new StringBuilder();

            bufferedReader.lines().forEach(str -> stringBuilder.append(str).append("\n"));

            return stringBuilder.toString();
        } catch (IOException e) {
            logger.error("Error while retrieving contents of the private key file located at: {}", this.privateKeyLocation);
        }
        return "";
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
}


