package io.github.edmm.plugins.puppet.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Paths;
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
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
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
        this.prepareMaster();
        this.configurePuppetMaster();
        this.prepareNodes();
    }

    private void prepareMaster() {
        this.setMasterHostName();
        this.setMasterId();
        this.setPuppetVersion();
        this.setCreatedAtTimestamp();
    }

    private void prepareNodes() {
        this.setNodes();
        this.setNodeFacts();
        this.setNodeState();
    }

    private void configurePuppetMaster() {
        this.copyPuppetModule();
        this.unzipPuppetModule();
        this.movePuppetModuleToProduction();
        this.transferAndExecPuppetSiteScript();
        this.generateSSHKeyPair();
        this.copyPublicKeyToPuppetModule();
        this.readGeneratedKeys();
    }

    private void unzipPuppetModule() {
        try {
            ChannelExec channelExec = this.setupChannelExec();

            channelExec.setCommand(Commands.UNZIP_PUPPET_MODULE + ";" + Commands.DELETE_ZIP);
            channelExec.connect();
        } catch (JSchException e) {
            throw new InstanceTransformationException("Failed to query data from Puppet Master. Please make sure that PuppetDB on Puppet Master is up and running.");
        }
    }

    private void movePuppetModuleToProduction() {
        try {
            ChannelExec channelExec = this.setupChannelExec();

            channelExec.setCommand(Commands.MOVE_PUPPET_MODULE);
            channelExec.connect();
        } catch (JSchException e) {
            throw new InstanceTransformationException("Failed to query data from Puppet Master. Please make sure that PuppetDB on Puppet Master is up and running.");
        }
    }

    private void setNodeFacts() {
        this.nodes.forEach(node -> node.setFacts(this.getFactsForNodeByCertName(node.getCertname())));
    }

    private void setNodeState() {
        this.nodes.forEach(node -> node.setState(PuppetState.NodeState.valueOf(node.getLatest_report_status())));

    }

    private void setMasterId() {
        this.id = String.valueOf((this.hostName + this.ip).hashCode());
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

    private void generateSSHKeyPair() {
        try {
            ChannelExec channelExec = this.setupChannelExec();
            BufferedReader reader = new BufferedReader(new InputStreamReader(channelExec.getInputStream()));

            channelExec.setCommand(Commands.generateSSHKeyPairWithCertName("puppet"));
            channelExec.connect();
        } catch (JSchException | IOException e) {
            throw new InstanceTransformationException("Failed to generate SSHKeyPair");
        }
    }

    private void copyPublicKeyToPuppetModule() {
        try {
            ChannelExec channelExec = this.setupChannelExec();
            channelExec.setCommand(Commands.COPY_PUBLIC_KEY);
            channelExec.connect();
        } catch (JSchException e) {
            throw new InstanceTransformationException("Failed to copy public key");
        }
    }

    private void readGeneratedKeys() {
        this.readPublicKey();
        this.readPrivateKey();
    }

    private void readPrivateKey() {
        try {
            ChannelSftp sftp = (ChannelSftp) this.session.openChannel("sftp");
            sftp.connect();
            // TODO make this less brittle
            InputStream stream = sftp.get("/home/ubuntu/.ssh/puppet");
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder stringBuilder = new StringBuilder();
            String line = reader.readLine();
            while (line != null) {
                stringBuilder.append(line).append("\n");
                line = reader.readLine();
            }
            this.generatedPrivateKey = stringBuilder.toString();
        } catch (JSchException | SftpException | IOException e) {

        }
    }

    private void readPublicKey() {
        try {
            ChannelSftp sftp = (ChannelSftp) this.session.openChannel("sftp");
            sftp.connect();
            // TODO make this less brittle
            InputStream stream = sftp.get("/home/ubuntu/.ssh/puppet.pub");
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            this.generatedPublicKey = reader.readLine();
        } catch (JSchException | SftpException | IOException e) {
            throw new InstanceTransformationException("Failed to retrieve public or private key from master", e.getCause());
        }
    }

    private void copyPuppetModule() {
        try {
            ChannelSftp channelSftp = (ChannelSftp) this.session.openChannel("sftp");
            channelSftp.connect();

            channelSftp.put(String.valueOf(Paths.get(ClassLoader.getSystemResource("edimm_ssh.zip").toURI())), "/home/ubuntu/");
        } catch (JSchException | SftpException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void transferAndExecPuppetSiteScript() {
        try {
            ChannelSftp channelSftp = (ChannelSftp) this.session.openChannel("sftp");
            channelSftp.connect();

            channelSftp.put(String.valueOf(Paths.get(ClassLoader.getSystemResource("edimm_ssh.sh").toURI())), "/home/ubuntu/");
            ChannelExec channelExec = this.setupChannelExec();
            channelExec.setCommand("sudo chmod +x edimm_ssh.sh; sudo ./edimm_ssh.sh");
            channelExec.connect();
        } catch (JSchException | SftpException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
