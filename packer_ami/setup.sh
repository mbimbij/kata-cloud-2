#! /bin/bash

sleep 30

sudo apt update

# installing some utilities
sudo apt install tree ncdu -y

# installing java
sudo apt install openjdk-14-jdk -y

# installing codedeploy agent
sudo apt-get install ruby wget -y
wget https:// aws-codedeploy-eu-west-3.s3.eu-west-3.amazonaws.com/latest/install
chmod +x ./install
sudo ./install auto > /tmp/logfile
sudo systemctl start codedeploy-agent
sudo systemctl enable codedeploy-agent

# creating / updating firewall rules
sudo ufw allow ssh
sudo ufw allow http
sudo ufw allow https
sudo ufw enable