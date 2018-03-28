package com.djordjem.serialmonitor.gui;

import com.djordjem.serialmonitor.serialport.SerialPortDTO;
import com.djordjem.serialmonitor.serialport.SerialPortEventListener;
import com.djordjem.serialmonitor.serialport.SerialPortService;
import com.djordjem.serialmonitor.settings.Settings;

import javax.swing.*;
import java.awt.event.*;
import java.util.List;
import java.util.stream.IntStream;

import static com.djordjem.serialmonitor.constants.Constants.POSSIBLE_BAUDRATES;
import static com.djordjem.serialmonitor.settings.SettingsService.SETTINGS;

public class SerialMonitor extends JDialog implements SerialPortEventListener {

  // Models
  private Settings settings;
  private DefaultListModel<String> historyListModel;
  private CustomComboModel<SerialPortDTO> portsCmbModel;

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

  private GuiUpdater guiUpdater = new GuiUpdater(this);

  public SerialMonitor() {
    super(null, java.awt.Dialog.ModalityType.TOOLKIT_MODAL);
    settings = SETTINGS.getSettings();

    portsCmbModel = new CustomComboModel<>();
    serialPortsCmb.setModel(portsCmbModel);

    historyListModel = new DefaultListModel<>();
    historyList.setModel(historyListModel);

    setContentPane(contentPane);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setTitle("Serial monitor");
    initPorts();
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

  /**
   * Verifies if new COM ports are available and updates combo box with new ports.
   */
  void initPorts() {
    if (needsReloadingPortList()) {
      List<SerialPortDTO> ports = SerialPortService.INSTANCE.getPorts();
      SerialPortDTO previoslySelectedPort = (SerialPortDTO) serialPortsCmb.getSelectedItem();
      serialPortsCmb.removeAllItems();
      for (SerialPortDTO sp : ports) {
        serialPortsCmb.addItem(sp);
        if (sp.equals(previoslySelectedPort)) {
          serialPortsCmb.setSelectedItem(sp);
        }
      }
    }
  }

  private boolean needsReloadingPortList() {
    List<SerialPortDTO> systemPorts = SerialPortService.INSTANCE.getPorts();
    List<SerialPortDTO> comboBoxPorts = portsCmbModel.getAllItems();
    return !(comboBoxPorts.containsAll(systemPorts) && comboBoxPorts.size() == systemPorts.size());
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
          sendChar(e.getKeyChar());
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
    historyList.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent evt) {
        JList list = (JList) evt.getSource();
        if (evt.getClickCount() == 2) {
          int index = list.locationToIndex(evt.getPoint());
          sendLine(historyListModel.get(index), false);
        }
      }
    });
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        onApplicationExit();
      }
    });
  }

  private void sendEnteredText() {
    String text = textFieldLineToSend.getText();
    sendLine(text, true);
    clearSendField();
  }

  private void sendLine(String line, boolean addToHistory) {
    SerialPortService.INSTANCE.sendLine(line, getNewLine());
    if (addToHistory) {
      historyListModel.insertElementAt(line, 0);
    }
  }

  private void sendChar(char c) {
    SerialPortService.INSTANCE.sendChar(c);
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
    int historySize = historyListModel.getSize();
    settings.getHistory().clear();
    IntStream.range(0, historySize).forEach(i -> settings.getHistory().add(historyListModel.getElementAt(i)));
    final SerialPortDTO selectedPort = (SerialPortDTO) serialPortsCmb.getSelectedItem();
    if (selectedPort != null) {
      settings.setPortName(selectedPort.getSystemPortName());
    }
    SETTINGS.flushToFile();
  }
}
