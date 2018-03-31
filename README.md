# Serial monitor

Yet another serial port terminal written as a replacement for Arduino serial monitor. 
It features a history of sent commands so they can be easily resent on doubleclik without 
need to retype them. It can send text as a line (press enter or click "send") or as a stream of characters 
as you type them.

**Currently tested on windows only. On Linux machines it might be needed to start this app with SUDO or it might not work at all. This issue will be resolved in the future.**

[Java JRE](https://java.com/en/download/) (version 8 or greater) is required to launch this application.

![Screenshot](https://raw.githubusercontent.com/jokam85/serialmonitor/master/docs/Screenshot_1.jpg)

## Releases

## v1.2 (TODO)
* command shortcuts and shortcut groups
* command shortcuts and shortcut groups - quick add from history
* command shortcuts and shortcut groups editing
* lock history entries (prevent them frome being cleared) (TODO)
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
* menu (save log as, save history as, shortcut editing) (HIGH)
* history maximum number of entries (HIGH)
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
