package io.github.edmm.plugins.puppet.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import io.github.edmm.core.transformation.InstanceTransformationException;
import io.github.edmm.plugins.puppet.model.Master;
import io.github.edmm.util.Util;

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
        this.executeSimpleCommand(Commands.INSTALL_UNZIP);
        this.executeSimpleCommand(Commands.UNZIP_PUPPET_MODULE);
        this.executeSimpleCommand(Commands.DELETE_ZIP);
        this.executeSimpleCommand(Commands.MOVE_PUPPET_MODULE);
        this.transferAndExecPuppetSiteScript();
        this.generateSSHKeyPair();
        this.copyPublicKeyToPuppetModule();
        // only supported on Puppet Enterprise...
        this.executeSimpleCommand(Commands.TRANSFER_KEYS_TO_NODES);
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
        this.master.setGeneratedPublicKey(
            this.readFileFromSFTP(this.outputLocation + ".ssh/puppet.pub")
        );
        this.master.setGeneratedPrivateKey(
            this.readFileFromSFTP(this.outputLocation + ".ssh/puppet")
        );
    }

    private String readFileFromSFTP(String path) {
        try {
            ChannelSftp sftp = this.setupChannelSftp();
            sftp.connect();

            try (InputStream stream = sftp.get(path)) {
                return Util.readFromStream(stream);
            }
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
