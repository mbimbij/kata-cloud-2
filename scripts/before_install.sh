#! /bin/bash

mkdir -p /opt/myapp/conf/

rm -rf /etc/systemd/system/myapp.service
rm -rf /opt/myapp/conf/application-server.yml
rm -rf /opt/myapp/myapp.jar
