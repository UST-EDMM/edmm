package io.github.edmm.plugins.puppet.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import io.github.edmm.core.plugin.ApiInteractor;
import io.github.edmm.core.transformation.InstanceTransformationException;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class ApiInteractorImpl implements ApiInteractor {
    private Session session;

    public ApiInteractorImpl(Session session) {
        this.session = session;
    }

    @Override
    public Object getDeployment() {
        try {
            ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
            InputStream in = channelExec.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            // test command
            channelExec.setCommand("curl http://localhost:8080/pdb/query/v4/reports");
            channelExec.connect();
            System.out.println(reader.readLine());
        } catch (JSchException | IOException e) {
            throw new InstanceTransformationException("Failed to query data from Puppet Master. Please make sure that PuppetDB on Puppet Master is up and running.");
        }
        return null;
    }

    @Override
    public Object getComponents() {
        return null;
    }

    @Override
    public Object getModel() {
        return null;
    }
}
