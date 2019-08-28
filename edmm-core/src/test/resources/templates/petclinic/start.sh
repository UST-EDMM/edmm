#!/bin/bash
find . -type f -name petclinic.war | xargs \
     java -Dspring.profiles.active=mysql \
          -Dspring.datasource.url=jdbc:mysql://${MYSQL_DB_HOST}/${MYSQL_DB_NAME} \
          -Dspring.datasource.username=${MYSQL_DB_USER} \
          -Dspring.datasource.password=${MYSQL_DB_PASSWORD} \
          -jar
exit 0
