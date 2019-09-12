# AWS CloudFormation Plugin

The  AWS CloudFormation plugin reflects their deployment model elements as a Java data model.
To achieve this we utilize a public Java library that exposes all the CloudFormation model elements to Java.
The Java data model is finally used to serialize a valid YAML output that can be processed by AWS CloudFormation.

For EDMM `compute` components, the plugin creates respective EC2 compute instances.
Further, respective security group resources are created and related to the compute instances. 

EDMM software components are transformed into `ConfigSets` that are applied to the respective compute instance.
Whenever software components specify ports, the corresponding ingress setting is defined on the security group.
All properties of the corresponding stack, including connected stacks, are serialized as environment variables to `/etc/environment`.
