# Puppet Plugin

## Notes

* Creates one module per non-compute component
* The main entry point is a component class which groups together classes that represent tasks
* For every task a separate class is created
* Task classes handle file copy and shell script execution operations that correspond to the provided lifecycle interface operations
