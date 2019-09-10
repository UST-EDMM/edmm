# Heat Orchestration Template Plugin

The OpenStack Heat plugin reflects their deployment model elements as a Java data model.
The Java data model is finally used to serialize a valid YAML output that can be processed by OpenStack.

For EDMM `compute` components, the plugin creates respective Nova compute instances.
The plugin does not create resources for OpenStack security groups and networks, instead this information is mapped as input parameters to the template. 

EDMM software components are all handled similarly.
For each, we create a `OS::Heat::SoftwareDeployment` resource and respective `OS::Heat::SoftwareConfig` resources for defined EDMM operations. 

## Validation

The generated template can be validated using the `heat-cli`:

```shell
openstack orchestration template validate \
    --os-auth-url "http://<url>:5000/v2.0" --insecure \
    --os-username "<user>" \
    --os-password "<password>" \
    --os-project-name "<tenant_name>" \
    -t "<relative_file_path>" \
    [--parameter <name>=<value>]
```

## Limitations

  * Only the first artifact of an operations is considered at the moment
  * Order of operations is not considered at the moment
  * Properties from an underlying and connected stacks are not provided as script inputs
