# SaltStack Plugin

The SaltStack plugin creates one formula for each compute module.

The topology graph is first reverted and sorted topologically to determine the deployment order.
Afterwards, multiple states are created for each component to copy files and run scripts.
Further, all properties are injected as system environment variable in order to be accessed by operations script.

The SaltStack plugin assumes that there is already a running virtual compute resource available with Salt Master and Salt Minon configured.

## Limitations

The /slat/top.sls file must be modified to meet the needs of the specific infrastructure.

For now, the IP addresses are generated statically and entered in the minion configuration scripts manually.
