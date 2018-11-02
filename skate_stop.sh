#!/usr/bin/env bash
set -e

p1=$1

if [ ! -n "$p1" ] ;then
    echo "you have not input a parameter. eg. test local or web!"
    exit
fi

BASE_IMAGE_PREFIX=""
SKATE_VERSION="latest"

if [ $p1 = "test" ] ;then
	echo "stop test demo!"
	IMAGE_PREFIX="192.168.31.34/skate/"
elif [ $p1 = "local" ] ;then
	echo "stop local develop demo!"
	IMAGE_PREFIX="skate/"
elif [ $p1 = "web" ] ;then
	echo "stop public web demo!"
	IMAGE_PREFIX="demoregistry.dataman-inc.com/skate/"
	BASE_IMAGE_PREFIX="demoregistry.dataman-inc.com/skate/"
	SKATE_VERSION=":latest"
else
    echo "you have not input a parameter. eg. test local or web!"
    exit
fi

export SKATE_VERSION IMAGE_PREFIX BASE_IMAGE_PREFIX

#env IMAGE_PREFIX='192.168.31.34/skate' SKATE_VERSION='latest' docker-compose -f docker-compose.yml down -v
docker-compose -f docker-compose.yml down -v
