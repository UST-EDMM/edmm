# Azure Plugin

## Notes
* Creates one `VirtualNetwork` and one `StorageAccount` for the whole set of required `VirtualMachines`.
* Creates one `Subnet` only.
* Specifies `NetworkSecurityGroup` at the level of the `NetworkInterface` of individual `VirtualMachines`.
* Creates a `SecurityRule` for every port required by applications hosted on the `VirtualMachine`.
* Creates one `VirtualMachineExtension` for every artifact associated with the lifecycle operations of the components. 
* Support OS authentication using either a password or ssh.