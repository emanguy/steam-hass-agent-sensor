# Steam go-hass-agent sensors for Home Assistant

This project provides a script and java JAR file for reporting your Steam client upload status
to Home Assistant via [go-hass-agent](https://github.com/joshuar/go-hass-agent)
on Linux.
It reads the Steam content_log.txt and determines the upload status that way. It also uses the Steam Store API
to pull the name of the app being updated and a URL for the game art.

It provides a sensor called "Steam Update Status" which can either have the state `idle`, `updating`, or `updated`.
It also includes attributes which expose the name of the game being updated and the URL for the game art.

## Installation

You'll need to build the Kotlin JAR file manually first, then copy the provided `steam-sensor.sh` file
to the [go-hass-agent scripts directory](https://github.com/joshuar/go-hass-agent?tab=readme-ov-file#-script-sensors).
This should be `~/.config/go-hass-agent/scripts/`.

To build the JAR file, run `./amper package`. When the process is complete you can find it in the
`build/tasks/_steam-hass-agent-sensor_executableJarJvm`
folder. Copy the JAR file to a separate directory in the go-hass-agent folder: `~/.config/go-hass-agent/steam-sensor/`.
This is necessary so go-hass-agent doesn't try to run the JAR file directly.

## Configuration

The Kotlin JAR file expects the Steam `content_log.txt` file to be at its standard location for the Steam
Flatpak installation: `~/.var/app/com.valvesoftware.Steam/data/Steam/logs/content_log.txt`.

If you have a different Steam installation (i.e. via the .deb package), you can set the environment variable
`STEAM_CONTENT_LOG`
in the `steam-sensor.sh` file to point to the correct location.
