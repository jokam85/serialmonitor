package com.djordjem.serialmonitor.gui;

import com.djordjem.serialmonitor.serialport.SerialPortDTO;
import com.djordjem.serialmonitor.serialport.SerialPortDataListener;
import com.djordjem.serialmonitor.serialport.SerialPortService;
import com.djordjem.serialmonitor.settings.CommandGroup;
import com.djordjem.serialmonitor.settings.Settings;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.stream.IntStream;

import static com.djordjem.serialmonitor.constants.Constants.POSSIBLE_BAUDRATES;
import static com.djordjem.serialmonitor.settings.SettingsService.SETTINGS;

public class SerialMonitor extends JDialog implements SerialPortDataListener {

  // Models
  private Settings settings;
  private CustomListModel<String> historyListModel;
  SerialPortComboModel portsCmbModel;
  CustomComboModel<CommandGroup> commandGroupsComboBoxModel;

  JPanel contentPane;
  JComboBox<SerialPortDTO> serialPortsCmb;
  JButton openPortBtn;
  JComboBox<Integer> baudRateCmb;
  JButton closeButton;
  JButton clearButton;
  JTextArea serialText;
  JCheckBox checkBoxAutoscroll;
  JTextField textFieldLineToSend;
  JButton buttonSend;
  JComboBox comboBoxLineEnding;
  JCheckBox checkBoxSendAsType;
  JPanel jPanelFooter;
  JList<String> historyList;
  JSplitPane historyTextSplit;
  JButton clearHistorybutton;
  JComboBox<CommandGroup> commandGroupsComboBox;
  JPanel commandButtonContainerPanel;

  private GuiUpdater guiUpdater = new GuiUpdater(this);

  public SerialMonitor() {
    super(null, java.awt.Dialog.ModalityType.TOOLKIT_MODAL);
    settings = SETTINGS.getSettings();

    portsCmbModel = new SerialPortComboModel();
    serialPortsCmb.setModel(portsCmbModel);

    historyListModel = new CustomListModel<>();
    historyList.setModel(historyListModel);

    commandGroupsComboBoxModel = new CustomComboModel<>();
    commandGroupsComboBox.setModel(commandGroupsComboBoxModel);

    setContentPane(contentPane);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setTitle("Serial monitor");
    initRates();
    initListeners();
    applySettings();

    guiUpdater.start();
    SerialPortService.INSTANCE.addDataListener(this);
  }

  @Override
  public void onNewData(byte[] newData) {
    String data = new String(newData);
    serialText.append(data);
    if (checkBoxAutoscroll.isSelected()) {
      serialText.setCaretPosition(serialText.getDocument().getLength());
    }
  }

  private void openPort() {
    SerialPortDTO selectedItem = (SerialPortDTO) serialPortsCmb.getSelectedItem();
    if (selectedItem != null) {
      String portName = selectedItem.getSystemPortName();
      Integer baudRate = (Integer) baudRateCmb.getSelectedItem();
      int baud = baudRate == null ? 9600 : baudRate;
      SerialPortService.INSTANCE.openPort(portName, baud);
    }
  }

  private void onApplicationExit() {
    guiUpdater.stop();
    SerialPortService.INSTANCE.closePort();
    SerialPortService.INSTANCE.removeDataListener(this);
    dispose();
    saveSettings();
  }

  private void initRates() {
    for (int rate : POSSIBLE_BAUDRATES) {
      baudRateCmb.addItem(rate);
    }
    baudRateCmb.setSelectedItem(settings.getBaudRate());
  }

  private void initListeners() {
    openPortBtn.addActionListener(e -> openPort());
    closeButton.addActionListener(e -> SerialPortService.INSTANCE.closePort());
    clearButton.addActionListener(e -> serialText.setText(""));
    buttonSend.addActionListener(e -> sendEnteredText());
    clearHistorybutton.addActionListener(e -> historyListModel.clear());
    textFieldLineToSend.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (checkBoxSendAsType.isSelected()) {
          SerialPortService.INSTANCE.sendChar(e.getKeyChar());
        } else {
          if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            buttonSend.doClick();
          }
        }
      }

      @Override
      public void keyReleased(KeyEvent e) {
        if (checkBoxSendAsType.isSelected()) {
          clearSendField();
        }
      }
    });
    historyList.addMouseListener(new HistoryListClickListener(this));

    commandGroupsComboBox.addActionListener(event -> renderShortcutButtons());
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        onApplicationExit();
      }
    });
  }

  private void sendEnteredText() {
    sendLine(textFieldLineToSend.getText(), true, true);
    clearSendField();
  }

  void sendLine(String line, boolean includeLineSeparator, boolean addToHistory) {
    SerialPortService.INSTANCE.sendLine(line, includeLineSeparator ? getNewLine() : null);
    if (addToHistory) {
      historyListModel.addAtTop(line, true, settings.getMaxHistoryEntries());
    }
  }

  private void clearSendField() {
    textFieldLineToSend.setText("");
  }

  private String getNewLine() {
    String nlSeparator = (String) comboBoxLineEnding.getSelectedItem();
    if (nlSeparator == null) {
      return "";
    }
    if (nlSeparator.equals("NL")) {
      return "\n";
    }
    if (nlSeparator.equals("CR")) {
      return "\r";
    }
    if (nlSeparator.equals("NL+CR")) {
      return "\n\r";
    }
    return "";
  }

  private void applySettings() {
    String portName = settings.getPortName();
    if (portName != null) {
      serialPortsCmb.setSelectedItem(SerialPortService.INSTANCE.getPort(settings.getPortName()));
    }
    settings.getHistory().forEach(historyListModel::addElement);
    settings.getGroups().forEach((name, group) -> commandGroupsComboBoxModel.addElement(group));
    checkBoxAutoscroll.setSelected(settings.getAutoscroll());
    checkBoxSendAsType.setSelected(settings.getSendAsYouType());
    comboBoxLineEnding.setSelectedItem(settings.getLineEnding());
    historyTextSplit.setDividerLocation(settings.getHistoryTextSeparatorPosition());
  }

  private void saveSettings() {
    settings.setPosX(getX());
    settings.setPosY(getY());
    settings.setWidth(getWidth());
    settings.setHeight(getHeight());
    settings.setBaudRate((Integer) baudRateCmb.getSelectedItem());
    settings.setAutoscroll(checkBoxAutoscroll.isSelected());
    settings.setSendAsYouType(checkBoxSendAsType.isSelected());
    settings.setLineEnding((String) comboBoxLineEnding.getSelectedItem());
    settings.setHistoryTextSeparatorPosition(historyTextSplit.getDividerLocation());

    // Copy history to settings object
    int historySize = historyListModel.getSize();
    settings.getHistory().clear();
    IntStream.range(0, historySize).forEach(i -> settings.getHistory().add(historyListModel.getElementAt(i)));

    // Selected port
    final SerialPortDTO selectedPort = (SerialPortDTO) serialPortsCmb.getSelectedItem();
    if (selectedPort != null) {
      settings.setPortName(selectedPort.getSystemPortName());
    }

    // Groups
    settings.getGroups().clear();
    commandGroupsComboBoxModel.getAllItems().forEach(settings::addGroup);

    SETTINGS.flushToFile();
  }

  void renderShortcutButtons() {
    CommandGroup cg = (CommandGroup) commandGroupsComboBoxModel.getSelectedItem();
    commandButtonContainerPanel.removeAll();
    if (cg != null) {
      boolean portOpen = SerialPortService.INSTANCE.isPortOpen();
      cg.getCommands().forEach(cmd -> {
        JButton cmdBtn = new JButton(cmd);
        cmdBtn.setEnabled(portOpen);
        commandButtonContainerPanel.add(cmdBtn);
        cmdBtn.addActionListener(e -> sendLine(cmd, true, true));
      });
    }
    commandButtonContainerPanel.updateUI();
  }

}
