# vento-remote

This is a small automation tool for Blauberg ventilation unit Vento Expert with WiFi (VENTO Expert A50-1 W).
It allows creating scheduled scenario to turn on and turn off the ventilation. 

See also [Spec](https://blaubergventilatoren.de/uploads/download/ventoexpertduowsmarthousev11ru.pdf).

Supported architecture is [ARM32v6](https://github.com/mbryzhko/vento-remote/tree/arm32v6).

## How To Use
**Select host architecture.** There are following types supported:
- x86_64
- arm32v7
- [arm32v6](https://github.com/mbryzhko/vento-remote/tree/arm32v6)

Checkout the latest version of [vento-remote-arm32v6](https://github.com/mbryzhko/vento-remote/pkgs/container/vento-remote%2Fvento-remote-arm32v6) release and set env var: `VENTO_VERSION`.  
```
export VENTO_VERSION=latest # or specific version
export VENTO_IMAGE=ghcr.io/mbryzhko/vento-remote/vento-remote-arm32v6
```

**Create config file.** For example:
```
durability:
  enable: true # enable durability, default: false
  storeFolderPath: "/usr/share/vento-remote"
scenario:
  - name: Night
    cron: 0 0 23 ? * *
    commands:
      - type: TURN_ON
        host: 192.168.1.101
      - type: TURN_ON
        host: 192.168.1.102
  - name: Morning
    cron: 0 0 8 ? * *
    commands:
      - type: TURN_OFF
        host: 192.168.1.101
      - type: TURN_OFF
        host: 192.168.1.102 
```

**Specify config file location.**  
`export VENTO_CONFIG_PATH=/home/pi/vento/config.yaml`

**Run.**  
`./run.sh`

