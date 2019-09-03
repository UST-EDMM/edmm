# Ansible Plugin

The Ansible plugin creates one playbook for the entire application topology.
The topology graph is first reverted and sorted topologically to determine the deployment order.
Afterwards, a play is created for each component.
As play tasks represent operations, they form together a logical EDMM component.
Further, all properties are included as play variables in order be accessed by operations, while all available operations are executed using the respective Ansible task.

The Ansible plugin assumes that there is already a running virtual compute resource available.
However, we could use Ansible's EC2 provisioner to spin up EC2 instances, but this is something that will be improved in future work.
