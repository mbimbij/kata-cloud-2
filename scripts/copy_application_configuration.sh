#! /bin/bash
cp conf/server/application-server.yml /etc/systemd/system/
systemctl daemon-reload
