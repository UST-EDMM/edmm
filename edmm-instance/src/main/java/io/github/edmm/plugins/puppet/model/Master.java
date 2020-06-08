package io.github.edmm.plugins.puppet.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.github.edmm.core.transformation.InstanceTransformationException;
import io.github.edmm.model.edimm.ComponentInstance;
import io.github.edmm.plugins.puppet.util.Commands;
import io.github.edmm.plugins.puppet.util.GsonHelper;
import io.github.edmm.plugins.puppet.util.PuppetPropertiesHandler;
import io.github.edmm.util.CastUtil;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// TODO: hide these ugly implementation details via factory pattern
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
        this.setMasterHostName();
        this.setMasterId();
        this.setPuppetVersion();
        this.setCreatedAtTimestamp();
    }

    private void setMasterHostName() {
        this.hostName = this.buildMasterNameFromString(this.executeCommandAndHandleResult(Commands.GET_MASTER));
    }

    private void setMasterId() {
        this.id = String.valueOf((this.hostName + this.ip).hashCode());
    }

    private void setPuppetVersion() {
        this.puppetVersion = this.executeCommandAndHandleResult(Commands.GET_VERSION);
    }

    private void setCreatedAtTimestamp() {
        this.createdAtTimestamp = this.executeCommandAndHandleResult(Commands.GET_CREATED_AT_TIMESTAMP);
    }

    private void setupSSH() {
        MasterSSHConfigurator masterSSHConfigurator = new MasterSSHConfigurator(this);
        masterSSHConfigurator.configurePuppetMaster();
    }

    private void setupNodes() {
        this.setNodes();
        this.setNodeFacts();
        this.setNodeState();
    }

    private void setNodes() {
        this.nodes = this.buildNodesFromString(this.executeCommandAndHandleResult(Commands.GET_NODES));
    }

    private void setNodeFacts() {
        this.nodes.forEach(node -> node.setFacts(this.getFactsForNodeByCertName(node.getCertname())));
    }

    private List<Fact> getFactsForNodeByCertName(String certName) {
        List<Fact> facts = new ArrayList<>();

        facts.add(this.getFact(certName, FactType.IPAddress));
        facts.add(this.getFact(certName, FactType.OperatingSystem));
        facts.add(this.getFact(certName, FactType.OperatingSystemRelease));
        facts.add(new Fact(certName, "privateKey", this.generatedPrivateKey));
        facts.add(new Fact(certName, "publicKey", this.generatedPublicKey));

        return facts;
    }

    private Fact getFact(String certName, FactType factType) {
        return buildFactFromString(this.executeCommandAndHandleResult(Commands.getFactCommandByFactType(certName, factType)));
    }

    private void setNodeState() {
        this.nodes.forEach(node -> node.setState(PuppetState.NodeState.valueOf(node.getLatest_report_status())));

    }

    private ChannelExec setupChannelExec() throws JSchException {
        return (ChannelExec) this.session.openChannel("exec");
    }

    private String buildMasterNameFromString(String jsonString) {
        Map<String, String> masterNameKeyValuePair = CastUtil.safelyCastToStringStringMap(GsonHelper.parseJsonStringToObjectType(jsonString.substring(1, jsonString.length() - 1), Map.class));
        return masterNameKeyValuePair.get("name");
    }

    private List<Node> buildNodesFromString(String jsonString) {
        return GsonHelper.parseJsonStringToParameterizedList(jsonString, Node.class);
    }

    private Fact buildFactFromString(String jsonString) {
        return GsonHelper.parseJsonStringToObjectType(jsonString.substring(1, jsonString.length() - 1), Fact.class);
    }

    private String executeCommandAndHandleResult(String command) {
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
