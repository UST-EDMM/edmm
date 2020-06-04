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

    public Master(String user, String ip, String privateKeyLocation, Integer sshPort) {
        this.user = user;
        this.ip = ip;
        this.privateKeyLocation = privateKeyLocation;
        this.sshPort = sshPort;
    }

    public ComponentInstance toComponentInstance() {
        ComponentInstance componentInstance = new ComponentInstance();
        componentInstance.setId(String.valueOf((this.hostName + this.ip).hashCode()));
        componentInstance.setName(this.hostName);
        componentInstance.setCreatedAt(this.createdAtTimestamp);
        componentInstance.setType(this.operatingSystem + this.operatingSystemRelease);
        // TODO state
        componentInstance.setInstanceProperties(PuppetPropertiesHandler.getComponentInstanceProperties(this.hostName, this.user, this.ip, this.privateKeyLocation, this.sshPort));

        return componentInstance;
    }

    public void connectToMaster() {
        try {
            JSch jsch = new JSch();
            jsch.addIdentity(this.privateKeyLocation);
            this.session = jsch.getSession(this.user, this.ip, this.sshPort);
            this.session.setConfig("StrictHostKeyChecking", "no");
            this.session.connect();

            this.initMasterData();
        } catch (JSchException e) {
            throw new InstanceTransformationException("Failed to connect with Puppet Master. Please make sure the correct user, host, sshPort and private key location of the Puppet Master is set.");
        }
    }

    private void initMasterData() {
        this.setMasterHostName();
        this.setNodes();
        this.setPuppetVersion();
        this.setCreatedAtTimestamp();
        this.nodes.forEach(node -> node.setFacts(this.getFactsForNodeByCertName(node.getCertname())));
        this.nodes.forEach(node -> node.setState(PuppetState.NodeState.valueOf(this.getStateForNodeByReportHash(node.getLatest_report_hash()))));
    }

    private void setNodes() {
        try {
            ChannelExec channelExec = this.setupChannelExec();
            BufferedReader reader = new BufferedReader(new InputStreamReader(channelExec.getInputStream()));

            channelExec.setCommand(Commands.GET_NODES);
            channelExec.connect();

            this.nodes = this.buildNodesFromString(reader.readLine());
        } catch (JSchException | IOException e) {
            throw new InstanceTransformationException("Failed to query data from Puppet Master. Please make sure that PuppetDB on Puppet Master is up and running.");
        }
    }

    private void setCreatedAtTimestamp() {
        try {
            ChannelExec channelExec = this.setupChannelExec();
            BufferedReader reader = new BufferedReader(new InputStreamReader(channelExec.getInputStream()));

            channelExec.setCommand(Commands.GET_CREATED_AT_TIMESTAMP);
            channelExec.connect();

            this.createdAtTimestamp = reader.readLine();
        } catch (JSchException | IOException e) {
            throw new InstanceTransformationException("Failed to query data from Puppet Master. Please make sure that PuppetDB on Puppet Master is up and running.");
        }
    }

    private void setPuppetVersion() {
        try {
            ChannelExec channelExec = this.setupChannelExec();
            channelExec.setPty(true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(channelExec.getInputStream()));
            channelExec.setCommand(Commands.GET_VERSION);
            channelExec.connect();
            this.puppetVersion = reader.readLine();
        } catch (JSchException | IOException e) {
            throw new InstanceTransformationException("Failed to query data from Puppet Master. Please make sure that PuppetDB on Puppet Master is up and running.");
        }
    }

    private void setMasterHostName() {
        try {
            ChannelExec channelExec = this.setupChannelExec();
            BufferedReader reader = new BufferedReader(new InputStreamReader(channelExec.getInputStream()));

            channelExec.setCommand(Commands.GET_MASTER);
            channelExec.connect();

            this.hostName = buildMasterNameFromString(reader.readLine());
        } catch (JSchException | IOException e) {
            throw new InstanceTransformationException("Failed to query data from Puppet Master. Please make sure that PuppetDB on Puppet Master is up and running.");
        }
    }

    private String getStateForNodeByReportHash(String reportHash) {
        try {
            ChannelExec channelExec = this.setupChannelExec();
            BufferedReader reader = new BufferedReader(new InputStreamReader(channelExec.getInputStream()));

            channelExec.setCommand(Commands.getNodeStateByReportHash(reportHash));
            channelExec.connect();

            return buildStateFromString(reader.readLine()).get("status");
        } catch (JSchException | IOException e) {
            throw new InstanceTransformationException("Failed to query data from Puppet Master. Please make sure that PuppetDB on Puppet Master is up and running.");
        }
    }

    private List<Fact> getFactsForNodeByCertName(String certName) {
        List<Fact> facts = new ArrayList<>();

        facts.add(this.getFact(certName, FactType.IPAddress));
        facts.add(this.getFact(certName, FactType.OperatingSystem));
        facts.add(this.getFact(certName, FactType.OperatingSystemRelease));
        facts.add(this.getFact(certName, FactType.SSHDSAKey));
        facts.add(this.getFact(certName, FactType.SSHRSAKey));

        return facts;
    }

    private Fact getFact(String certName, FactType factType) {
        try {
            ChannelExec channelExec = this.setupChannelExec();
            BufferedReader reader = new BufferedReader(new InputStreamReader(channelExec.getInputStream()));

            channelExec.setCommand(Commands.getFactCommandByFactType(certName, factType));
            channelExec.connect();

            return buildFactFromString(reader.readLine());
        } catch (JSchException | IOException e) {
            throw new InstanceTransformationException("Failed to query data from Puppet Master. Please make sure that PuppetDB on Puppet Master is up and running.");
        }
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

    private Map<String, String> buildStateFromString(String jsonString) {
        return GsonHelper.parseJsonStringToStringStringMap(jsonString.substring(1, jsonString.length() - 1));
    }
}
