#!/bin/bash

cd /home/ubuntu/action/dawheen_backend/


echo "> docker-compose 중지" >> /home/ubuntu/action/deploy.log

# 이전에 실행 중인 컨테이너를 중지하고 삭제합니다.
sudo docker-compose down

# 이전에 실행 중인 컨테이너의 네트워크 및 볼륨을 정리합니다.
sudo docker network prune -f

echo "> docker-compose 작동" >> /home/ubuntu/action/deploy.log


sudo docker-compose up -d --remove-orphans


