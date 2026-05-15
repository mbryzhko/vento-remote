#!/bin/bash

VENTO_CONFIG_PATH=${VENTO_CONFIG_PATH:-"$(pwd)/src/main/resources/schedule.yaml"}
VENTO_VERSION=${VENTO_VERSION:-"latest"}
VENTO_IMAGE=${VENTO_IMAGE:-"ghcr.io/mbryzhko/vento-remote/vento-remote-arm32v7"}
VENTO_TZ="Europe/Kiev"
if [ -f "/etc/timezone" ]; then
    VENTO_TZ="$(cat /etc/timezone)"
fi

if sudo docker ps -a --format '{{.Names}}' | grep -q '^vento-remote$'; then
    echo "Stopping and removing existing vento-remote container..."
    sudo docker stop vento-remote
    sudo docker rm vento-remote
fi

sudo docker run -d --name vento-remote -v $VENTO_CONFIG_PATH:/usr/share/vento-remote/schedule.yaml \
  -e VENTO_SCHEDULE="file:/usr/share/vento-remote/schedule.yaml" \
  -e TZ="$VENTO_TZ" \
  --restart=always \
  --security-opt seccomp=custom-seccomp.json \
  "$VENTO_IMAGE":"$VENTO_VERSION"
