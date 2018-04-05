# Serial monitor

Yet another serial port terminal written as a replacement for Arduino serial monitor. 
It features a history of sent commands so they can be easily resent on doubleclik without 
need to retype them. It can send text as a line (press enter or click "send") or as a stream of characters 
as you type them.

**Currently tested on windows only. On Linux machines it might be needed to start this app with SUDO, 
due to permissions required for accessing serial ports.**

[Java JRE](https://java.com/en/download/) (version 8 or greater) is required to launch this application.

![Screenshot](https://raw.githubusercontent.com/jokam85/serialmonitor/master/docs/Screenshot_1_2.jpg)

## Releases

## 1.4 TODO
* display special chars (\n, \r) (TODO)
* display connection status (TODO)
* display errors when sending fails (TODO)
* command labels (TODO)
* saving command groups to separate file, outside of settings file(TODO)
* BUGFIX offscreen command buttons. break them into rows (TODO)
* BUGFIX disable save as history button when history is empty (TODO)

## v1.3 [download](https://github.com/jokam85/serialmonitor/releases/download/v1.3/serialmonitor.jar)
* save history as
* display errors port fails to open

## v1.2 [download](https://github.com/jokam85/serialmonitor/releases/download/v1.2/serialmonitor.jar)
* commands and command groups edit
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

## Planned features and bugfixes (TODO)
* chat mode (show sent messages)
* apend to file
* pattern recognition (highlight or show only pattern)
* executing external programs on pattern recognized
* autoconnect to new port if not connected
* sniffer (passthrough mode)
* copy/paste
