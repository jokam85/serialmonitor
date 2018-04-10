package com.djordjem.serialmonitor.gui.maindlg;

import com.djordjem.serialmonitor.Main;
import com.djordjem.serialmonitor.gui.models.CustomComboModel;
import com.djordjem.serialmonitor.serialport.SerialPortDTO;
import com.djordjem.serialmonitor.serialport.SerialPortsListListener;
import com.djordjem.serialmonitor.settings.SettingsService;

import java.util.List;

public class SerialPortComboModelList extends CustomComboModel<SerialPortDTO> implements SerialPortsListListener {

  private boolean firstInitialisationDone = false;

  public SerialPortComboModelList() {
    Main.SERIAL_PORT_SERVICE.addPortsChangedListener(this);
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
      String lastUsedPortName = SettingsService.SETTINGS.getSettings().getPortName();
      if (!firstInitialisationDone && sp.getName().equals(lastUsedPortName)) {
        setSelectedItem(sp);
      }
    }
    firstInitialisationDone = true;
  }

}
