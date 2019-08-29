#!/bin/bash

function check () {
  echo "Check if "$1" is available"
  if ! [[ -x "$(command -v "$1")" ]]; then
    echo "INFO: "$1" is not installed" >&2
    return 1
  fi
}

check systemctl
if [[ $? -eq 0 ]]; then
    systemctl daemon-reload
    systemctl enable mysql
    systemctl start mysql
    systemctl status mysql
else
    echo "INFO: Starting MySQL daemon in foreground"
    mysqld
fi
exit 0
