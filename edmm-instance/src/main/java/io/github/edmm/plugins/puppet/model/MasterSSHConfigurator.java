package io.github.edmm.plugins.puppet.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import io.github.edmm.core.transformation.InstanceTransformationException;
import io.github.edmm.plugins.puppet.util.Commands;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

class MasterSSHConfigurator {
    private Master master;
    private Session session;

    MasterSSHConfigurator(Master master) {
        this.master = master;
        this.session = master.getSession();
    }

    void configurePuppetMaster() {
        this.copyPuppetModule();
        this.unzipPuppetModule();
        this.deleteZip();
        this.movePuppetModuleToProduction();
        this.transferAndExecPuppetSiteScript();
        this.generateSSHKeyPair();
        this.copyPublicKeyToPuppetModule();
        this.readGeneratedKeys();
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
            ChannelSftp channelSftp = (ChannelSftp) this.session.openChannel("sftp");
            channelSftp.connect();
            channelSftp.put(String.valueOf(Paths.get(ClassLoader.getSystemResource("edimm_ssh.sh").toURI())), "/home/ubuntu/");

            this.executeSimpleCommand(Commands.EXECUTE_HELPER_SCRIPT);
        } catch (JSchException | SftpException | URISyntaxException e) {
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
            this.master.setGeneratedPrivateKey(stringBuilder.toString());
        } catch (JSchException | SftpException | IOException e) {
            throw new InstanceTransformationException("failed to retrieve private key from master", e.getCause());
        }
    }

    private void readPublicKey() {
        try {
            ChannelSftp sftp = (ChannelSftp) this.session.openChannel("sftp");
            sftp.connect();
            // TODO make this less brittle
            InputStream stream = sftp.get("/home/ubuntu/.ssh/puppet.pub");
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            this.master.setGeneratedPublicKey(reader.readLine());
        } catch (JSchException | SftpException | IOException e) {
            throw new InstanceTransformationException("Failed to retrieve public key from master", e.getCause());
        }
    }

    private void executeSimpleCommand(String command) {
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
}
