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

## Limitation

OpenStack Heat cannot upload plain artifacts and handle them with assigned operations.
As a workaround, an EDMM model has to supply the respective `create` operations that download the artifacts from an accessible location.
This also requires, that the artifacts are publicly staged to such a location, which is not done during the transformation process. 
