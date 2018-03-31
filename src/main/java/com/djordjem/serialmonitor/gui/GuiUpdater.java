package com.djordjem.serialmonitor.gui;

import com.djordjem.serialmonitor.serialport.SerialPortService;

import javax.swing.*;
import java.util.Arrays;

public class GuiUpdater {

  private SerialMonitor serialMonitor;

  private Timer guiUpdateTimer = new Timer(250, (event) -> {
    boolean isPortOpened = SerialPortService.INSTANCE.isPortOpen();
    serialMonitor.serialPortsCmb.setEnabled(!isPortOpened);
    serialMonitor.baudRateCmb.setEnabled(!isPortOpened);
    serialMonitor.openPortBtn.setVisible(!isPortOpened);
    serialMonitor.closeButton.setVisible(isPortOpened);
    serialMonitor.textFieldLineToSend.setEnabled(isPortOpened);
    serialMonitor.buttonSend.setEnabled(isPortOpened && !serialMonitor.checkBoxSendAsType.isSelected());
    serialMonitor.historyList.setEnabled(isPortOpened);

    Arrays.stream(serialMonitor.commandButtonContainerPanel.getComponents()).forEach(component -> component.setEnabled(isPortOpened));
  });

  GuiUpdater(SerialMonitor serialMonitor) {
    this.serialMonitor = serialMonitor;
  }

  void start() {
    guiUpdateTimer.start();
  }

  void stop() {
    guiUpdateTimer.stop();
  }

}
