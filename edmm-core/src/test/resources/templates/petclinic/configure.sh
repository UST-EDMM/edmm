#!/bin/bash
find . -type f -name '*.war' -exec cp {} /opt/tomcat/latest/webapps \;
exit 0
