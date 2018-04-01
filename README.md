# Serial monitor

Yet another serial port terminal written as a replacement for Arduino serial monitor. 
It features a history of sent commands so they can be easily resent on doubleclik without 
need to retype them. It can send text as a line (press enter or click "send") or as a stream of characters 
as you type them.

**Currently tested on windows only. On Linux machines it might be needed to start this app with SUDO, 
due to permissions required for accessing serial ports.**

[Java JRE](https://java.com/en/download/) (version 8 or greater) is required to launch this application.

![Screenshot](https://raw.githubusercontent.com/jokam85/serialmonitor/master/docs/Screenshot_1.jpg)

## Releases

## v1.2 [download](https://github.com/jokam85/serialmonitor/releases/download/v1.2/serialmonitor.jar)
* commands and command groups edit (TODO)
* commands and command groups
* commands and command groups - quick add from history
* commands and command groups editing
* BUGFIX do not add sequential duplicates to history

## v1.1 [download](https://github.com/jokam85/serialmonitor/releases/download/v1.1/serialmonitor.jar)
* data sending
* new line separator
* data sending on enter or as typed
* sending history
* BUGFIX using swing timer instead of thread for GUI update
* BUGFIX clear input field when "check as you type" option is enabled

## v1.0 [download](https://github.com/jokam85/serialmonitor/releases/download/v1.0/serialmonitor.jar)
* simple textual console monitoring
* remember interface state and settings in user folder
* all possible baudrates
* autoscroll

## Planned features (TODO)
* save log as, save history as
* chat mode (show sent messages)
* display special chars
* display connection status
* display errors when sending fails
* display errors port fails to open
* save window content to file
* apend to file
* pattern recognition (highlight or show only pattern)
* RTS, CTS...
* executing external programs on pattern recognized
* autoconnect to new port if not connected
* binary data (byte separation)
* limited and configurable console window content
* settings menu
* sniffer (passthrough mode)
* copy/paste
