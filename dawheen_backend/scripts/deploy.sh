#!/bin/bash

cd /home/ubuntu/action/dawheen/

echo "> docker-compose 작동" >> /home/ubuntu/action/deploy.log

docker-compose up -d
