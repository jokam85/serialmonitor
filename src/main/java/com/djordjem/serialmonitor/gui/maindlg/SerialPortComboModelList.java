package com.djordjem.serialmonitor.gui.maindlg;

import com.djordjem.serialmonitor.gui.models.CustomComboModel;
import com.djordjem.serialmonitor.serialport.SerialPortDTO;
import com.djordjem.serialmonitor.serialport.SerialPortService;
import com.djordjem.serialmonitor.serialport.SerialPortsListListener;
import com.djordjem.serialmonitor.settings.SettingsService;

import java.util.List;

public class SerialPortComboModelList extends CustomComboModel<SerialPortDTO> implements SerialPortsListListener {

  private boolean firstInitialisationDone = false;

  public SerialPortComboModelList() {
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
      if (!firstInitialisationDone && sp.getSystemPortName().equals(SettingsService.SETTINGS.getSettings().getPortName())) {
        setSelectedItem(sp);
      }
    }
    firstInitialisationDone = true;
  }

}
