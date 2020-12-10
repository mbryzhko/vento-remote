#!/bin/bash

VENTO_CONFIG_PATH=${VENTO_CONFIG_PATH:-"./src/main/resources/schedule.yaml"}
VENTO_VERSION=${VENTO_VERSION:-"1.7"}

docker run -d --name vento-remote -v ${VENTO_CONFIG_PATH}:/usr/share/vento-remote/schedule.yaml \
  -e VENTO_SCHEDULE="file:/usr/share/vento-remote/schedule.yaml" \
  -e TZ="$(date +%Z)" \
  docker.pkg.github.com/mbryzhko/vento-remote/vento-remote-arm32v6:$VENTO_VERSION