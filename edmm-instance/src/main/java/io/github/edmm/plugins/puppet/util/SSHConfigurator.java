package io.github.edmm.plugins.puppet.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import io.github.edmm.core.transformation.InstanceTransformationException;
import io.github.edmm.plugins.puppet.model.Master;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class SSHConfigurator {

    private Master master;
    private Session session;
    private String outputLocation;

    public SSHConfigurator(Master master) {
        this.master = master;
        this.session = master.getSession();
        this.outputLocation = "/home/" + this.master.getUser() + "/";
    }

    public void configurePuppetMaster() {
        this.copyPuppetModuleToMaster();
        this.unzipPuppetModule();
        this.deleteZip();
        this.movePuppetModuleToProduction();
        this.transferAndExecPuppetSiteScript();
        this.generateSSHKeyPair();
        this.copyPublicKeyToPuppetModule();
        this.readGeneratedKeys();
    }

    private void copyPuppetModuleToMaster() {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            URL resource = classLoader.getResource("edimm_ssh.zip");
            ChannelSftp channelSftp = this.setupChannelSftp();
            channelSftp.connect();
            channelSftp.put(resource.getFile(), this.outputLocation);
        } catch (JSchException | SftpException e) {
            e.printStackTrace();
        }
    }

    private void unzipPuppetModule() {
        this.executeSimpleCommand(Commands.UNZIP_PUPPET_MODULE);
    }

    private void deleteZip() {
        this.executeSimpleCommand(Commands.DELETE_ZIP);
    }

    private void movePuppetModuleToProduction() {
        this.executeSimpleCommand(Commands.MOVE_PUPPET_MODULE);
    }

    private void transferAndExecPuppetSiteScript() {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            URL resource = classLoader.getResource("edimm_ssh.sh");
            ChannelSftp channelSftp = this.setupChannelSftp();
            channelSftp.connect();
            channelSftp.put(resource.getFile(), this.outputLocation);

            this.executeSimpleCommand(Commands.EXECUTE_HELPER_SCRIPT);
        } catch (JSchException | SftpException e) {
            e.printStackTrace();
        }
    }

    private void generateSSHKeyPair() {
        this.executeSimpleCommand(Commands.generateSSHKeyPairWithCertName("puppet"));
    }

    private void copyPublicKeyToPuppetModule() {
        this.executeSimpleCommand(Commands.COPY_PUBLIC_KEY);
    }

    private void readGeneratedKeys() {
        this.readPublicKey();
        this.readPrivateKey();
    }

    private void readPrivateKey() {
        try {
            ChannelSftp sftp = this.setupChannelSftp();
            sftp.connect();
            // TODO make this less brittle
            InputStream stream = sftp.get(this.outputLocation + ".ssh/puppet");
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder stringBuilder = new StringBuilder();
            String line = reader.readLine();
            while (line != null) {
                stringBuilder.append(line).append("\n");
                line = reader.readLine();
            }
            this.master.setGeneratedPrivateKey(stringBuilder.toString());
        } catch (JSchException | SftpException | IOException e) {
            throw new InstanceTransformationException("failed to retrieve private key from master", e.getCause());
        }
    }

    private void readPublicKey() {
        try {
            ChannelSftp sftp = this.setupChannelSftp();
            sftp.connect();
            // TODO make this less brittle
            InputStream stream = sftp.get(this.outputLocation + ".ssh/puppet.pub");
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            this.master.setGeneratedPublicKey(reader.readLine());
        } catch (JSchException | SftpException | IOException e) {
            throw new InstanceTransformationException("Failed to retrieve public key from master", e.getCause());
        }
    }

    public void executeSimpleCommand(String command) {
        try {
            ChannelExec channelExec = this.setupChannelExec();

            channelExec.setCommand(command);
            channelExec.connect();
        } catch (JSchException e) {
            throw new InstanceTransformationException("Failed to query data from Puppet Master. Please make sure that PuppetDB on Puppet Master is up and running.");
        }
    }

    private ChannelExec setupChannelExec() throws JSchException {
        return (ChannelExec) this.session.openChannel("exec");
    }

    private ChannelSftp setupChannelSftp() throws JSchException {
        return (ChannelSftp) this.session.openChannel("sftp");
    }
}
