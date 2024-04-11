#!/bin/bash



echo "> docker-compose 작동" >> /home/ubuntu/action/deploy.log

cd /home/ubuntu/action/dawheen_backend/

sudo docker-compose up -d
