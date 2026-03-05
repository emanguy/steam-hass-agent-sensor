#!/usr/bin/env bash

if ! which java >>/dev/null; then
  echo "ERROR: You need java installed on your system to run the Steam sensor."
  exit 1
fi

java -jar ~/.config/go-hass-agent/steam-sensor/steam-hass-agent-sensor-jvm-executable.jar
