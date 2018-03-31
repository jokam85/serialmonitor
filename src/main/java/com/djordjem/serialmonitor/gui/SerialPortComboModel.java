package com.djordjem.serialmonitor.gui;

import com.djordjem.serialmonitor.serialport.SerialPortDTO;
import com.djordjem.serialmonitor.serialport.SerialPortService;

import java.util.List;

public class SerialPortComboModel extends CustomComboModel<SerialPortDTO> {

  /**
   * Verifies if new COM ports are available and updates combo box with new ports.
   */
  void checkIfPortListChanged() {
    if (needsReloadingPortList()) {
      List<SerialPortDTO> ports = SerialPortService.INSTANCE.getPorts();
      SerialPortDTO previoslySelectedPort = (SerialPortDTO) getSelectedItem();
      this.removeAllElements();
      for (SerialPortDTO sp : ports) {
        this.addElement(sp);
        if (sp.equals(previoslySelectedPort)) {
          setSelectedItem(sp);
        }
      }
    }
  }

  private boolean needsReloadingPortList() {
    List<SerialPortDTO> systemPorts = SerialPortService.INSTANCE.getPorts();
    List<SerialPortDTO> comboBoxPorts = getAllItems();
    return !(comboBoxPorts.containsAll(systemPorts) && comboBoxPorts.size() == systemPorts.size());
  }

}
