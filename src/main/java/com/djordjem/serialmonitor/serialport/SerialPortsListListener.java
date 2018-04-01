package com.djordjem.serialmonitor.serialport;

import java.util.List;

public interface SerialPortsListListener {

  void portListChanged(List<SerialPortDTO> serialPorts);

}
