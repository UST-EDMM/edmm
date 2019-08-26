# Terraform Plugin

## Limitations

* Properties of all related components need to be transferred into an `env.sh` script.
  This script will be then sourced by each operations.
  The following template is used: `'target_name'_'property_name'`
* Use `local-exec` provisioner to [extract](https://learn.hashicorp.com/terraform/getting-started/provision) the ip address of a dependant compute instance (target node).
  This intel is added to the `env.sh` script in the form `'target_name'_HOSTNAME`.
