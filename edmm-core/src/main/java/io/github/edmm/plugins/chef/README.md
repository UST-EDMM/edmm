# Chef Plugin

## Notes

* A chef repository is created for a given topology
* The topology is split by distinct stacks based on the HostedOn relationship
* For every compute component of each stack chef-provisioning-based recipe is generated
* For every non-compute component a shell script execution recipe is generated
