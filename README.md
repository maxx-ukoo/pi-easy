# Pi-Control

[![Build Status](https://travis-ci.org/joemccann/dillinger.svg?branch=master)](https://travis-ci.org/joemccann/dillinger)

Pi-Control is java based app to control pins state via HTTP or MQTT protocols with web UI

# Endpoints

## HTTP
HTTP commands endpoint:
```sh
http://ip:port/control?cmd=<command>
```

## MQTT
MQTT command topic
```sh
/cmd
```

# Command reference

## GPIO
GPIO,<gpio>,<value>

<gpio> - pin address (number - 0,1,2,3...)
<value> - pin state (0,1)

### Example

| Command | HTTP | MQTT |
| ------ | ------ | ------ |
| GPIO,<gpio>,<value> | http://localhost:8080/control?cmd=GPIO,12,1 | GPIO,12,1



License
----

MIT GPLv3



###Links

[The JavaFX based MQTT Client](https://mqttfx.jensd.de/)
