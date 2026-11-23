# Nothing OS AI & Card Widget Limit Adjuster

A lightweight developer utility to adjust the default AI and Card widget cap on Nothing OS and CMF Phone devices without root permissions.

## Overview

Nothing OS includes an integrated online configuration system used by the launcher to set dynamic limits. By default, certain devices fall back to an internal limit of 2 widgets.

Instead of modifying system binaries or breaking package signatures, this tool communicates with the local configuration provider (`content://com.nothing.proxy.onlineconfig.provider`) using the device's native runtime to update the widget parameter dynamically.

## Requirements

- A Nothing Phone or CMF Phone device
- Developer Options & USB Debugging (or Wireless Debugging) enabled
- ADB installed on your computer

## Quick Start

### Linux & macOS
```bash
git clone https://github.com/musabtvt/nothing-widget-limit-unlocker.git
cd nothing-widget-limit-unlocker
./unlock.sh 7
```

### Windows
```cmd
unlock.bat 7
```

You can replace `7` with any preferred target limit.

## How It Works

1. **Payload Generation**: Formats the target configuration object with `maxAiWidgetNumber`.
2. **Local Provider Storage**: Injects the value into the device's local configuration provider.
3. **Dynamic Reload**: The launcher's configuration observer detects the update and adjusts runtime limits without requiring a system reboot.

## License

This project is licensed under the Apache 2.0 License.
