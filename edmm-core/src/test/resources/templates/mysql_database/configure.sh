#!/bin/bash
find . -type f -name '*.sql' | xargs | mysql --password=${MYSQL_ROOT_PASSWORD}
exit 0
