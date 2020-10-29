# Readme

This prototype is able to transform running applications deployed with Kubernetes, Puppet, AWS CloudFormation, or OpenStack Heat to a TOSCA Service Template Instance with additional management functionality that is manageable with the OpenTOSCA ecosystem.

# Developer Setup
This guide aims to provide help how to setup the EDMMi CLI and how to verify its functionality using a simple setup with a Puppet deployment that is enriched with management functionality using the OpenTOSCA ecosystem.
In the first section, an explanation is given how to set up a simple Puppet deployment which can be enriched later.
In the second section, it is shown which settings are required to run this prototype, and how to build and run it eventually.
In the third section, it is shown how to manage the Puppet deployment with additional management functionality using the OpenTOSCA ecosystem which is used in this prototype.

## Setup Puppet deployment
To test this prototype, this section explains how to setup a simple deployment consisting of a Puppet master, and a Puppet agent which is managed by the master.
Further, a declarative deployment model is provided that installs a simple piece of software on the Puppet agent.

### Step 1 - Setup two VMs
To setup a Puppet master, and a Puppet agent, respectively one machine is required that is able to run the Puppet master, respectively the Puppet agent.
Puppet currently only supports *nix machines as master.
For the simplicity of this guide, i am going to use simply two VMs running on OpenStack: Ooe to run the Puppet master, and one to run the Puppet agent.

### Step 2 - Setup the Puppet Master
Now that we have a running VM with a *nix OS, this step shows how to setup the Puppet master on top of it.
Before Puppet is installed, a modification to the /etc/hosts file on both the master and the agent is required such that they can resolve each other.
So, for example my master's hostname is 'puppet-master', while my agent's hostname is 'puppet-agent'.

Following is the /etc/hosts file of the master:

![/etc/hosts file of the master](./doc/img/hosts_master.png)

Following is the /etc/hosts file of the agent:

![/etc/hosts file of the agent](./doc/img/hosts_agent.png)

Now, we can start to setup Puppet on the master. To do this, enter following command on the Puppet master:

```wget https://apt.puppetlabs.com/puppet6-release-bionic.deb```

Afterwards, execute following command to add and configure the Puppet repository:

```sudo dpkg -i puppet6-release-bionic.deb```

Followed by an update of the repository list:

```sudo apt update```

Now to actually install the Puppet master, we execute following command:

```sudo apt install -y puppetserver```

Puppet is installed now! Now we are going to configure the Puppet master. To do this, make following changes to the puppet.conf file (located in /etc/puppetlabs/puppet/):

![/etc/puppetlabs/puppet/puppet.conf file](./doc/img/puppetconf.png)

Now, we setup the certificate authority by running:

```sudo /opt/puppetlabs/bin/puppetserver ca setup```

Once this is finished, we can start the Puppet master with following two commands:

```sudo systemctl start puppetserver```

```sudo systemctl enable puppetserver```

### Step 3 - Setup the Puppet Agent

Enter the following commands on the Puppet Agent to install and setup Puppet.
First, download Puppet by following command:

```wget https://apt.puppetlabs.com/puppet6-release-bionic.deb```

Then install the package by executing following command:

```sudo dpkg -i puppet6-release-bionic.deb```

After this, execute this obligatory command:

```sudo apt update```

Then, we setup the VM such that it acts as a Puppet Agent.
This process is started by following command:

```sudo apt install -y puppet-agent```

Now, the Puppet Agent needs to be configured.
To do this, we edit the configuration file by running following command:

```sudo vim /etc/puppetlabs/puppet/puppet.conf```

Edit the file such that it looks like this:

![/etc/puppetlabs/puppet/puppet.conf file](./doc/img/puppetconf_agent.png)

Now it's time to start the Puppet Agent service by running this command:

```sudo /opt/puppetlabs/bin/puppet resource service puppet ensure=running enable=true```

The output should look as shown below:

![Output of starting the Puppet Agent](./doc/img/output.png)

### Step 4 - Connect the Puppet Agent to the Puppet Master
Once all previous steps are finished, we need to enable the communication between the Puppet Agent and the Puppet Master we just set up.
Enter following command on the Puppet Master, to check if a certificate request from the Puppet Agent was already received:

```sudo /opt/puppetlabs/bin/puppetserver ca list```

The output should be similar to this:

![Output of listing received certificates](./doc/img/output__.png)

The certificate request can then be signed by running following command:

```sudo /opt/puppetlabs/bin/puppetserver ca sign --certname puppet-agent```

The output should state that the certificate was signed successfully.
To check, run the following command on the Puppet Agent:

```sudo /opt/puppetlabs/bin/puppet agent --test```

The output should be as shown below:

![Output of testing the signing of the certificate](./doc/img/output_ok_.png)

That's it! The Puppet Master and the Agent are now up and running, and connected.
Now, it is possible to manage the Puppet Agent via the Master.
