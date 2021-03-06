package com.djordjem.serialmonitor.gui.maindlg;

import com.djordjem.serialmonitor.Main;

import javax.swing.*;
import java.util.Arrays;

public class GuiUpdater {

  private MainDialog mainDialog;

  private Timer guiUpdateTimer = new Timer(250, (event) -> doUpdate());

  GuiUpdater(MainDialog mainDialog) {
    this.mainDialog = mainDialog;
  }

  void start() {
    guiUpdateTimer.start();
  }

  void stop() {
    guiUpdateTimer.stop();
  }

  private void doUpdate() {
    boolean isPortOpened = Main.SERIAL_PORT_SERVICE.isPortOpen();
    mainDialog.serialPortsCmb.setEnabled(!isPortOpened);
    mainDialog.baudRateCmb.setEnabled(!isPortOpened);
    mainDialog.openPortBtn.setVisible(!isPortOpened);
    mainDialog.closeBtn.setVisible(isPortOpened);
    mainDialog.sendTextField.setEnabled(isPortOpened);
    mainDialog.sendButton.setEnabled(isPortOpened && !mainDialog.checkBoxSendAsType.isSelected());
    mainDialog.saveLogButton.setEnabled(mainDialog.serialText.getText().length() > 0);
    mainDialog.saveHistoryButton.setEnabled(mainDialog.historyList.getModel().getSize() > 0);
    Arrays.stream(mainDialog.commandButtonContainerPanel.getComponents()).forEach(component -> component.setEnabled(isPortOpened));
  }
}
