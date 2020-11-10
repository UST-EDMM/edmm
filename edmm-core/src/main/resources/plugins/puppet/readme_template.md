# Deployment with Puppet

Deploy the application with Puppet by placing the contents of this folder inside an environment of a Puppet server.
For example, to deploy the application in the `production` environment, copy everything to `/etc/puppetlabs/code/environments/production/` by executing:

```shell script
sudo cp -r ~/puppet/. /etc/puppetlabs/code/environments/production/
```

**NOTE**: It may be necessary to rename the nodes to the corresponding cert names of the actual running nodes in the `manifests/site.pp` file.
          However, if there is only a single node, it may be renamed to `default`:
```
node 'default' {
  include ...
}
```
          
