package io.github.edmm.plugins.puppet.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

import io.github.edmm.core.transformation.InstanceTransformationException;
import io.github.edmm.plugins.puppet.util.Commands;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.Getter;

public class Master {
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

    public String getReports() {
        try {
            ChannelExec channelExec = this.setupChannelExec();
            BufferedReader reader = new BufferedReader(new InputStreamReader(channelExec.getInputStream()));

            channelExec.setCommand(Commands.GET_REPORTS);
            channelExec.connect();
            return reader.readLine();
        } catch (JSchException | IOException e) {
            throw new InstanceTransformationException("Failed to query data from Puppet Master. Please make sure that PuppetDB on Puppet Master is up and running.");
        }
    }

    public List<Node> getNodes() {
        try {
            ChannelExec channelExec = this.setupChannelExec();
            BufferedReader reader = new BufferedReader(new InputStreamReader(channelExec.getInputStream()));

            channelExec.setCommand(Commands.GET_NODES);
            channelExec.connect();

            System.out.println(reader.readLine());
        } catch (JSchException | IOException e) {
            throw new InstanceTransformationException("Failed to query data from Puppet Master. Please make sure that PuppetDB on Puppet Master is up and running.");
        }
        return Collections.emptyList();
    }

    private ChannelExec setupChannelExec() throws JSchException {
        return (ChannelExec) this.session.openChannel("exec");
    }
}
