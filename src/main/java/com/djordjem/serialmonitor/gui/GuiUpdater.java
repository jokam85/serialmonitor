package com.djordjem.serialmonitor.gui;

import com.fazecast.jSerialComm.SerialPort;

import javax.swing.*;

public class GuiUpdater {

  private SerialMonitor serialMonitor;

  private Timer guiUpdateTimer = new Timer(250, (event) -> {
    SerialPort openedPort = serialMonitor.openedPort;
    serialMonitor.initPorts(false);
    serialMonitor.serialPortsCmb.setEnabled(openedPort == null || !openedPort.isOpen());
    serialMonitor.baudRateCmb.setEnabled(openedPort == null || !openedPort.isOpen());
    serialMonitor.openPortBtn.setVisible(openedPort == null || !openedPort.isOpen());
    serialMonitor.closeButton.setVisible(openedPort != null && openedPort.isOpen());
    serialMonitor.textFieldLineToSend.setEnabled(openedPort != null && openedPort.isOpen());
    serialMonitor.buttonSend.setEnabled(openedPort != null && openedPort.isOpen() && !serialMonitor.checkBoxSendAsType.isSelected());
    serialMonitor.historyList.setEnabled(openedPort != null && openedPort.isOpen());
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
