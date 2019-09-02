# Ansible Plugin

## Notes

* Creates one playbook for the entire application topology
* The topology graph is first reverted and sorted topologically to determine the deployment order
* A play is created per component
* Play's tasks represent operations and properties are included as play's variables  