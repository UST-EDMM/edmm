# Puppet Plugin

The strategy of the Puppet plugin is to create one module per non-compute component.
For Puppet we also assume a running virtual compute infrastructure.

The main entry point is a EDMM component class which groups together classes that represent tasks.
For every task a separate class is created.
Task classes handle file copy and shell script execution operations that correspond to the provided lifecycle interface operations of EDMM components.

The puppet plugin multiple template files and a simple Java data model to populate the content for the final deployment model. 
