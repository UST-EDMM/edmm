# Chef Plugin

The Chef plugin creates a Chef repository for a given topology based on multiple template files.
The EDMM topology is split by distinct stacks based on the `HostedOn` relation type.
For every EDMM `compute` component of each stack, a `chef-provisioning-based` recipe is generated.
At the moment, we only support Chef's Docker driver to generate a provisioner recipe.

The plugin collects all EDMM operation artifacts and creates a respective shell script execution recipe.
