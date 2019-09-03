# Docker Compose Plugin

The Docker Compose plugin is slightly different as Docker Compose and Docker Swarm only deal with containers as virtual compute resources.
Therefore, the plugin uses the same algorithm to identify the software component leaf nodes in the reversed graph as the Kubernetes plugin.
For each leaf, the complete stack is identified following EDMM's `HostedOn` relations.
Afterwards, a Dockerfile is generated.
The plugin tries to derive a perfect parent image, but whenever there are EDMM operations defined we fall back to `ubuntu` and define to execute the respective artifacts inside the Dockerfile.

After creating the Dockerfile, a `docker-compose.yml` file is generated based on a template. 
For each identified stack we create a `service` entry in the resulting file.

Moreover, all related component properties are injected as environment variables to the final container.
The respective service name of connected stacks is in addition injected in order to utilize Kubernetes' DNS to establish connections. 
