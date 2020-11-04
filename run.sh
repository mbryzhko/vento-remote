#!/bin/bash

docker run -v /Users/new/dev/src/vento-remote/schedule.yaml:/usr/share/vento-remote/schedule.yaml \
  -e VENTO_SCHEDULE="file:/usr/share/vento-remote/schedule.yaml" \
  mbryzhko/vento-remote:1.1-SNAPSHOT
