package com.djordjem.serialmonitor.serialport;

import java.util.List;

public interface SerialPortsListener {

  void portListChanged(List<SerialPortDTO> serialPorts);

}
