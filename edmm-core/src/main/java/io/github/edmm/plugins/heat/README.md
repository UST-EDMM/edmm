# Heat Orchestration Template Plugin

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

* Only the first artifact of an operations is considered
* Order of operations is not considered at the moment
* Properties from the underlying stack and connected stacks are not provided as script inputs
