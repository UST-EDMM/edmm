package io.github.edmm.plugins.puppet.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.github.edmm.core.transformation.InstanceTransformationException;
import io.github.edmm.plugins.puppet.util.Commands;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.Getter;

public class Master {
    private String hostName;
    private String user;
    private String ip;
    private String privateKeyLocation;
    private Integer port;
    private Session session;
    @Getter
    private List<Node> nodes;

    public Master(String user, String ip, String privateKeyLocation, Integer port) {
        this.user = user;
        this.ip = ip;
        this.privateKeyLocation = privateKeyLocation;
        this.port = port;
    }

    public void connectToMaster() {
        try {
            JSch jsch = new JSch();
            jsch.addIdentity(this.privateKeyLocation);
            this.session = jsch.getSession(this.user, this.ip, this.port);
            this.session.setConfig("StrictHostKeyChecking", "no");
            this.session.connect();
        } catch (JSchException e) {
            throw new InstanceTransformationException("Failed to connect with Puppet Master. Please make sure the correct user, host, port and private key location of the Puppet Master is set.");
        }
    }

    public List<Node> getNodes() {
        try {
            ChannelExec channelExec = this.setupChannelExec();
            BufferedReader reader = new BufferedReader(new InputStreamReader(channelExec.getInputStream()));

            channelExec.setCommand(Commands.GET_NODES);
            channelExec.connect();

            return this.buildNodesFromString(reader.readLine());
        } catch (JSchException | IOException e) {
            throw new InstanceTransformationException("Failed to query data from Puppet Master. Please make sure that PuppetDB on Puppet Master is up and running.");
        }
    }

    public void getMasterHostName() {
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

    private String buildMasterNameFromString(String jsonString) {
        Type masterNameType = new TypeToken<Map<String, String>>() {
        }.getType();
        Gson gson = new Gson();
        Map<String, String> typ = gson.fromJson(jsonString.substring(1, jsonString.length() - 1), masterNameType);
        return typ.get("name");
    }

    public List<Fact> getFactsForNodeByCertName(String certName) {
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

    private List<Node> buildNodesFromString(String jsonString) {
        Type nodeType = new TypeToken<ArrayList<Node>>() {
        }.getType();
        Gson gson = new Gson();
        return gson.fromJson(jsonString, nodeType);
    }

    private Fact buildFactFromString(String jsonString) {
        Type factType = new TypeToken<Fact>() {
        }.getType();
        Gson gson = new Gson();
        // the query always returns an array with ONE object, so we remove the array brackets and convert it as object
        return gson.fromJson(jsonString.substring(1, jsonString.length() - 1), factType);
    }
}
