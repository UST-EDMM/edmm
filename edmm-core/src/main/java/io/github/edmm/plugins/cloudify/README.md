# Cloudify Plugin

As Cloudify supports multiple providers, we only provide an implementation to make use of the Azure provider for spinning up compute instances.
The Cloudify plugin uses a template file and an Azure-based data model to populate a valid YAML deployment file.
A visitor implementation is used to "visit" all EDMM `compute` components.
For each of them, security groups and compute instances are created and respectively wired with a VPC and a subnet.

EDMM software components are instructed to be installed by using Cloudify's type `VirtualMachineExtension`.
This extension is added for each lifecycle operation defined for all EDMM software components.

The properties of EDMM components are respectively mapped to the corresponding Cloudify type property.
