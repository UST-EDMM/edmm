#!/bin/bash
if [[ -d "/docker-entrypoint-initdb.d" ]]; then
    find . -type f -name '*.sql' -exec cp {} /docker-entrypoint-initdb.d \;
else
    find . -type f -name '*.sql' | xargs | mysql -u root --password=${DBMS_ROOT_PASSWORD}
fi
exit 0
