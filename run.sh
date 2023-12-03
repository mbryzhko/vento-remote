#!/bin/bash

VENTO_CONFIG_PATH=${VENTO_CONFIG_PATH:-"$(pwd)/src/main/resources/schedule.yaml"}
VENTO_VERSION=${VENTO_VERSION:-"latest"}
VENTO_IMAGE=${VENTO_IMAGE:-"docker.pkg.github.com/mbryzhko/vento-remote/vento-remote-x86"}
VENTO_TZ="Europe/Kiev"
if [ -f "/etc/timezone" ]; then
    VENTO_TZ="$(cat /etc/timezone)"
fi

sudo docker run -d --name vento-remote -v $VENTO_CONFIG_PATH:/usr/share/vento-remote/schedule.yaml \
  -e VENTO_SCHEDULE="file:/usr/share/vento-remote/schedule.yaml" \
  -e TZ="$VENTO_TZ" \
  --restart=always \
  "$VENTO_IMAGE":"$VENTO_VERSION"
