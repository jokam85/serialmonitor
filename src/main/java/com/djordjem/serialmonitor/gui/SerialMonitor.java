package com.djordjem.serialmonitor.gui;

import com.djordjem.serialmonitor.settings.Settings;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.djordjem.serialmonitor.settings.SettingsService.SETTINGS;

public class SerialMonitor extends JDialog {

  // Ports
  private List<SerialPort> ports = new ArrayList<>();
  SerialPort openedPort = null;

  // Models
  private Settings settings;
  private DefaultListModel<String> historyListModel;

  private int[] rates = new int[]{110, 300, 600, 1200, 2400, 4800, 9600, 14400, 19200, 38400, 57600, 115200, 128000, 256000};

  JPanel contentPane;
  JComboBox<SerialPortCmbItem> serialPortsCmb;
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

  private SerialPortDataListener dataListener = new SerialPortDataListener() {
    public int getListeningEvents() {
      return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
    }

    public void serialEvent(SerialPortEvent event) {
      if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
        return;
      }
      try {
        byte[] newData = new byte[openedPort.bytesAvailable()];
        int numRead = openedPort.readBytes(newData, newData.length);
        if (numRead > 0) {
          String data = new String(newData);
          serialText.append(data);
          if (checkBoxAutoscroll.isSelected()) {
            serialText.setCaretPosition(serialText.getDocument().getLength());
          }
        }
      } catch (Exception e) {
        closePort();
        e.printStackTrace();
      }
    }
  };

  public SerialMonitor() {
    super(null, java.awt.Dialog.ModalityType.TOOLKIT_MODAL);
    settings = SETTINGS.getSettings();
    setContentPane(contentPane);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setTitle("Serial monitor");
    initPorts(true);
    initRates();
    initListeners();

    historyListModel = new DefaultListModel<>();
    historyList.setModel(historyListModel);
    settings.getHistory().forEach(historyListModel::addElement);

    checkBoxAutoscroll.setSelected(settings.getAutoscroll());
    checkBoxSendAsType.setSelected(settings.getSendAsYouType());
    comboBoxLineEnding.setSelectedItem(settings.getLineEnding());
    historyTextSplit.setDividerLocation(settings.getHistoryTextSeparatorPosition());
    guiUpdater.start();

  }

  private void openPort() {
    closePort();
    int index = serialPortsCmb.getSelectedIndex();
    openedPort = ports.get(index);
    Integer baudRate = (Integer) baudRateCmb.getSelectedItem();
    int baud = baudRate == null ? 9600 : baudRate;
    openedPort.setBaudRate(baud);
    openedPort.openPort();
    openedPort.addDataListener(dataListener);
  }

  private void closePort() {
    if (openedPort != null && openedPort.isOpen()) {
      openedPort.removeDataListener();
      openedPort.closePort();
      openedPort = null;
    }
  }

  private void onApplicationExit() {
    guiUpdater.stop();
    dispose();
    settings.setPosX(getX());
    settings.setPosY(getY());
    settings.setWidth(getWidth());
    settings.setHeight(getHeight());
    settings.setBaudRate((Integer) baudRateCmb.getSelectedItem());
    settings.setAutoscroll(checkBoxAutoscroll.isSelected());
    settings.setSendAsYouType(checkBoxSendAsType.isSelected());
    settings.setLineEnding(comboBoxLineEnding.getSelectedItem().toString());
    settings.setHistoryTextSeparatorPosition(historyTextSplit.getDividerLocation());
    int historySize = historyListModel.getSize();
    settings.getHistory().clear();
    IntStream.range(0, historySize).forEach(i -> settings.getHistory().add(historyListModel.getElementAt(i)));
    final SerialPortCmbItem selectedPort = (SerialPortCmbItem) serialPortsCmb.getSelectedItem();
    if (selectedPort != null) {
      settings.setPortName(selectedPort.getSerialPort().getSystemPortName());
    }
    SETTINGS.flushToFile();
  }

  void initPorts(boolean shouldSetDefaultValue) {
    if (needsReloadingPortList()) {
      this.ports = Arrays.asList(SerialPort.getCommPorts());
      SerialPortCmbItem previoslySelectedPort = (SerialPortCmbItem) serialPortsCmb.getSelectedItem();
      serialPortsCmb.removeAllItems();
      for (SerialPort sp : ports) {
        SerialPortCmbItem item = new SerialPortCmbItem(sp);
        serialPortsCmb.addItem(item);
        // Prethodno odabrani port
        if (previoslySelectedPort != null) {
          String selectedPortName = previoslySelectedPort.getSerialPort().getDescriptivePortName();
          if (sp.getDescriptivePortName().equals(selectedPortName)) {
            serialPortsCmb.setSelectedItem(item);
          }
        }
        // default
        if (shouldSetDefaultValue) {
          String portName = settings.getPortName();
          if (portName != null && portName.equals(item.getSerialPort().getSystemPortName())) {
            serialPortsCmb.setSelectedItem(item);
          }
        }
      }
    }
  }

  private boolean needsReloadingPortList() {
    List<SerialPort> currentSystemPorts = Arrays.asList(SerialPort.getCommPorts());
    List<String> systemPortNames = currentSystemPorts.stream().map(SerialPort::getDescriptivePortName).collect(Collectors.toList());
    List<String> comboPortNames = this.ports.stream().map(SerialPort::getDescriptivePortName).collect(Collectors.toList());
    return !(comboPortNames.containsAll(systemPortNames) && comboPortNames.size() == systemPortNames.size());
  }

  private void initRates() {
    for (int rate : rates) {
      baudRateCmb.addItem(rate);
    }
    baudRateCmb.setSelectedItem(settings.getBaudRate());
  }

  private void initListeners() {
    openPortBtn.addActionListener(e -> openPort());
    closeButton.addActionListener(e -> closePort());
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
    try {
      openedPort.getOutputStream().write(line.concat(getNewLine()).getBytes());
      if (addToHistory) {
        historyListModel.insertElementAt(line, 0);
      }
    } catch (IOException e1) {
      closePort();
      e1.printStackTrace();
    }
  }

  private void sendChar(char c) {
    try {
      openedPort.getOutputStream().write(c);
    } catch (IOException e1) {
      closePort();
      e1.printStackTrace();
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
}
