#!/usr/bin/env bash

set -e

p1=$1

if [ ! -n "$p1" ] ;then
    echo "you have not input a parameter eg. test local or web!"
    exit
fi

BASE_IMAGE_PREFIX=""

HOST_IP="192.168.31.46"
if [ $p1 = "test" ] ;then
	IMAGE_PREFIX="192.168.31.34/skate/"
	SKATE_VERSION="latest"
	WEB_IP="192.168.31.46"
elif [ $p1 = "local" ] ;then
	IMAGE_PREFIX="skate/"
	SKATE_VERSION="latest"
	WEB_IP="192.168.31.46"
elif [ $p1 = "web" ] ;then
	IMAGE_PREFIX="demoregistry.dataman-inc.com/skate/"
	BASE_IMAGE_PREFIX="demoregistry.dataman-inc.com/skate/"
	#下一行处需要手动修改版本号
	SKATE_VERSION=":latest"
	HOST_IP="10.3.8.23"
	WEB_IP="106.75.90.26"
else
    echo "you have not input a parameter in  'test'\'local'\'web'"
    exit
fi

# Export the active docker machine IP

#HOST_IP=`ifconfig | grep 'inet'| grep -v '127.0.0.1'|grep -v '172.' | cut -d: -f2 | awk '{ print $2}'|tr -s ["\n"]|tr -d [":"]`

# docker-machine doesn't exist in Linux, assign default ip if it's not set
DOCKER_IP=${HOST_IP:-192.168.31.46}
WEB_IP=${WEB_IP:-192.168.31.46}

export SKATE_VERSION DOCKER_IP WEB_IP IMAGE_PREFIX BASE_IMAGE_PREFIX

# Remove existing containers
docker-compose -f docker-compose.yml stop
docker-compose -f docker-compose.yml rm -f

# Start the config service first and wait for it to become available
docker-compose -f docker-compose.yml up -d config-service

while [ -z ${CONFIG_SERVICE_READY} ]; do
  echo "Waiting for config service..."
  if [ "$(curl --silent $DOCKER_IP:8888/health 2>&1 | grep -q '\"status\":\"UP\"'; echo $?)" = 0 ]; then
      CONFIG_SERVICE_READY=true;
  fi
  sleep 2
done

# Start the discovery service next and wait
docker-compose -f docker-compose.yml up -d discovery-service

while [ -z ${DISCOVERY_SERVICE_READY} ]; do
  echo "Waiting for discovery service..."
  if [ "$(curl --silent $DOCKER_IP:8761/health 2>&1 | grep -q '\"status\":\"UP\"'; echo $?)" = 0 ]; then
      DISCOVERY_SERVICE_READY=true;
  fi
  sleep 2
done

# Start the other containers
docker-compose -f docker-compose.yml up -d

# Attach to the log output of the cluster
#docker-compose -f docker-compose.yml logs
