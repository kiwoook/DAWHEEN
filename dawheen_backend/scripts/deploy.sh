#!/bin/bash

# shellcheck disable=SC2164
cd /home/ubuntu/action/dawheen_backend/

EXIST_BLUE=$(sudo docker-compose -p dawheen_backend-blue -f docker-compose.blue.yml ps | grep Up)

if [ -z "$EXIST_BLUE" ]; then
    echo "blue up"
    sudo docker-compose -p dawheen_backend-blue -f docker-compose.blue.yml up -d
    BEFORE_COMPOSE_COLOR="green"
    AFTER_COMPOSE_COLOR="blue"
else
    echo "green up"
    sudo docker-compose -p dawheen_backend-green -f docker-compose.green.yml up -d
    BEFORE_COMPOSE_COLOR="blue"
    AFTER_COMPOSE_COLOR="green"
fi

sleep 10

EXIST_AFTER=$(sudo docker-compose -p dawheen_backend-${AFTER_COMPOSE_COLOR} -f docker-compose.${AFTER_COMPOSE_COLOR}.yml ps | grep Up)

if [ -n "$EXIST_AFTER" ]; then
   # nginx.config 를 컨테이너에 맞게 변경해주고 reload 한다
    sudo cp /etc/nginx/dawheen_backend.${AFTER_COMPOSE_COLOR}.conf /etc/nginx/nginx.conf
    sudo nginx -s reload

    # 이전 컨테이너 종료
    sudo docker-compose -p dawheen_backend-${BEFORE_COMPOSE_COLOR} -f docker-compose.${BEFORE_COMPOSE_COLOR}.yml down
    echo "$BEFORE_COMPOSE_COLOR down"
fi
