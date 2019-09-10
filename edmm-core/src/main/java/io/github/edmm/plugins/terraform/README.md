# Terraform Plugin

As Terraform supports multiple providers, we only provide an implementation to use Terraform's AWS provider for spinning up EC2 instances.
The Terraform plugin uses a template file and a simple `Map`-based data model to populate a valid Terraform deployment file.
A visitor implementation is used to "visit" all EDMM `compute` components.
For each of them, a respective `aws_security_group` and `aws_instance` is generated.
Further, for all EDMM `compute` components a `default` VPC, subnet, and internet gateway is generated

EDMM software components are instructed to be installed by using Terraform provisioner.
The `file` provisioner is used to upload component artifacts and the `remote-exec` provisioner is used to define the invocation of defined operations.

The properties of all components are at the moment transferred into an `env.sh` script: `'component_name'_'property_name'`.
The idea is, that this can be sourced by each operation to access the information of components in case a connection needs to be established.

## Limitations

* The `env.sh` script needs to be sourced by EDMM operations individually

## Future Work

* Use `local-exec` provisioner to [extract](https://learn.hashicorp.com/terraform/getting-started/provision) the ip address of a dependant compute instance (target node).
  This intel is added to the `env.sh` script in the form `'component_name'_HOSTNAME`.
