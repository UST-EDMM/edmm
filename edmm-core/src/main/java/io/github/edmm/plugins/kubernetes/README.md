# Kubernetes Plugin

The Kubernetes plugin is slightly different than the others as Kubernetes only deals with containers as virtual compute resources.
Therefore, the plugin uses an algorithm to identify the software component leaf nodes in the reversed graph.
For each leaf, the complete stack is identified following EDMM's `HostedOn` relations.
Afterwards, a Dockerfile is generated.
The plugin tries to derive a perfect parent image, but whenever there are EDMM operations defined we fall back to `ubuntu` and define to execute the respective artifacts inside the Dockerfile.

After creating the Dockerfile, Kubernetes resources are created.
For each identified stack we create a Kubernetes `Deployment` and a corresponding `Service` and uses the `kubernetes-client` published by Fabric8 to create these Java data models and the resulting YAML.

Moreover, all related component properties are injected as environment variables to the final container.
The respective service name of connected stacks is in addition injected in order to utilize Kubernetes' DNS to establish connections. 
