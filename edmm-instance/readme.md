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