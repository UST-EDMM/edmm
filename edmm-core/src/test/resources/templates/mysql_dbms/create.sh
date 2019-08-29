#!/bin/bash
apt -y update
apt -y install mysql-server
# mysql_secure_installation < ${DBMS_ROOT_PASSWORD}
exit 0
