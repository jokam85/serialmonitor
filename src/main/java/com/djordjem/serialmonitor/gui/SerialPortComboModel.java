package com.djordjem.serialmonitor.gui;

import com.djordjem.serialmonitor.serialport.SerialPortDTO;
import com.djordjem.serialmonitor.serialport.SerialPortService;
import com.djordjem.serialmonitor.serialport.SerialPortsListener;

import java.util.List;

public class SerialPortComboModel extends CustomComboModel<SerialPortDTO> implements SerialPortsListener {

  public SerialPortComboModel() {
    SerialPortService.INSTANCE.addPortsChangedListener(this);
  }

  @Override
  public void portListChanged(List<SerialPortDTO> serialPorts) {
    SerialPortDTO previoslySelectedPort = (SerialPortDTO) getSelectedItem();
    this.removeAllElements();
    for (SerialPortDTO sp : serialPorts) {
      this.addElement(sp);
      if (sp.equals(previoslySelectedPort)) {
        setSelectedItem(sp);
      }
    }
  }

}
