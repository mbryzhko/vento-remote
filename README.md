# vento-remote-arm32v6

This is a small automation tool for Blauberg ventilation unit Vento Expert with WiFi (VENTO Expert A50-1 W).
It allows creating scheduled scenario to turn on and turn off the ventilation. 

See also [Spec](https://blaubergventilatoren.de/uploads/download/ventoexpertduowsmarthousev11ru.pdf).

## How To Use
### Install
Download run script.  
`wget https://github.com/mbryzhko/vento-remote/blob/arm32v6/run.sh`

Checkout the latest version of [vento-remote-arm32v6](https://github.com/mbryzhko/vento-remote/packages/526550/versions) release and set env var: `VENTO_VERSION`.  
`export VENTO_VERSION=1.9`

Create config file. For example:
```
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

Specify config file location.  
`export VENTO_CONFIG_PATH=/home/pi/vento/config.yaml`

Run.  
`./run.sh`

