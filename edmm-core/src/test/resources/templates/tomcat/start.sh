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
    /bin/cat <<EOM >/etc/systemd/system/tomcat.service
[Unit]
Description=Tomcat 9 servlet container
After=network.target

[Service]
Type=forking

User=tomcat
Group=tomcat

Environment="JAVA_HOME=/usr/lib/jvm/default-java"
Environment="JAVA_OPTS=-Djava.security.egd=file:///dev/urandom -Djava.awt.headless=true"

Environment="CATALINA_BASE=/opt/tomcat/latest"
Environment="CATALINA_HOME=/opt/tomcat/latest"
Environment="CATALINA_PID=/opt/tomcat/latest/temp/tomcat.pid"
Environment="CATALINA_OPTS=-Xms512M -Xmx1024M -server -XX:+UseParallelGC"

ExecStart=/opt/tomcat/latest/bin/startup.sh
ExecStop=/opt/tomcat/latest/bin/shutdown.sh

[Install]
WantedBy=multi-user.target
EOM
    systemctl daemon-reload
    systemctl enable tomcat
    systemctl start tomcat
    systemctl status tomcat
else
    echo "INFO: Starting Tomcat in foreground"
    export CATALINA_BASE="/opt/tomcat/latest"
    export CATALINA_HOME="/opt/tomcat/latest"
    export CATALINA_OPTS="-Xms512M -Xmx1024M -server -XX:+UseParallelGC"
    ${CATALINA_HOME}/bin/catalina.sh run
fi
exit 0
