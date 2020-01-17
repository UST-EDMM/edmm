# CFEngine Plugin

The CFEngine plugin creates one policy that runs on each host.

The topology graph is first reverted and sorted topologically to determine the deployment order.

Afterwards, for each host is generated:
* A class that identifies it;
* The environment variables that will be injected into the scripts;
* The methods that will be executed in sequence according to the deployment order.

The CFEngine plugin assumes that there is already a running virtual compute resource available with CFEngine master configured.

### Configuration
* Edit the generated `/deployment/deployment.cf` according to your host ips
* Put the `/deployment` directory in `/var/cfengine/masterfiles/`
* For CFEngine to know about your policy file and the bundle inside it, references to them should be added to promises.cf.
    ```
    $ sudo nano /var/cfengine/masterfiles/promises.cf
    ```
    Add `deployment/deployment.cf` at the end of the inputs list.
    ```
    inputs => {
        ...
                
        # List of services here
        "services/file_change.cf",
        
        "deployment/deployment.cf",
    };
    ```
    Additionally, `deployment` bundle should be mentioned in the bundlesequence list toward the top of the file.
    ```
    bundlesequence => {
        ...
        
        # Agent bundle
        cfe_internal_management,   # See cfe_internal/CFE_cfengine.cf
        service_catalogue,
        @(cfengine_enterprise_hub_ha.management_bundles),
        
        deployment,
    };
    ```
  Save the file and exit.
  
Your policy has now been added to the policy server, and will be run every five minutes.

The policy is structured in such a way that if the deployment directory is present it does nothing, so if you want to run the scripts again you have to delete it.
