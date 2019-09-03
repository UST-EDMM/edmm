# Azure Plugin

For the Azure plugin we built an internal Java model to represent virtual compute resources and software extensions on top of them.
By using a Java model, we easily can serialize it to a valid JSON document that Azure Resource Manager can process.

We create one `VirtualNetwork` and one `StorageAccount` for the whole set of required `VirtualMachine` objects.
However, we only create one `Subnet` for all virtual compute resources.
Further, a `NetworkSecurityGroup` is specified at the level of the `NetworkInterface` of each individual `VirtualMachine`.
`SecurityRule` objects are created for every port required by applications hosted on the `VirtualMachine`.
We therefore employ the visitor pattern to traverse the graph and find all required exposed ports.

As EDMM operations on components use artifacts to define installation steps, we create one `VirtualMachineExtension` for every artifact associated with the lifecycle operations of the components. 

The plugin currently supports OS authentication to be either a password or SSH.
