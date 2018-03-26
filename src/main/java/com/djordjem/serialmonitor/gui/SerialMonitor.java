package com.djordjem.serialmonitor.gui;

import com.djordjem.serialmonitor.settings.Settings;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.djordjem.serialmonitor.settings.SettingsService.SETTINGS;

public class SerialMonitor extends JDialog {

  private List<SerialPort> ports = new ArrayList<>();
  private SerialPort openedPort = null;

  private int[] rates = new int[]{110, 300, 600, 1200, 2400, 4800, 9600, 14400, 19200, 38400, 57600, 115200, 128000, 256000};

  private JPanel contentPane;
  private JComboBox<SerialPortCmbItem> serialPortsCmb;
  private JButton openPortBtn;
  private JComboBox<Integer> baudRateCmb;
  private JButton closeButton;
  private JButton clearButton;
  private JTextArea serialText;
  private JCheckBox checkBoxAutoscroll;
  private JTextField textFieldLineToSend;
  private JButton buttonSend;
  private JComboBox comboBoxLineEnding;
  private JCheckBox checkBoxSendAsType;
  private JPanel jPanelFooter;
  private Settings settings;

  private Timer guiUpdateTimer = new Timer(300, (event) -> {
    initPorts(false);
    serialPortsCmb.setEnabled(openedPort == null || !openedPort.isOpen());
    baudRateCmb.setEnabled(openedPort == null || !openedPort.isOpen());
    openPortBtn.setVisible(openedPort == null || !openedPort.isOpen());
    closeButton.setVisible(openedPort != null && openedPort.isOpen());
    textFieldLineToSend.setEnabled(openedPort != null && openedPort.isOpen());
    buttonSend.setEnabled(openedPort != null && openedPort.isOpen() && !checkBoxSendAsType.isSelected());
  });

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
    checkBoxAutoscroll.setSelected(settings.getAutoscroll());
    checkBoxSendAsType.setSelected(settings.getSendAsYouType());
    comboBoxLineEnding.setSelectedItem(settings.getLineEnding());
    guiUpdateTimer.start();
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
    guiUpdateTimer.stop();
    dispose();
    settings.setPosX(getX());
    settings.setPosY(getY());
    settings.setWidth(getWidth());
    settings.setHeight(getHeight());
    settings.setBaudRate((Integer) baudRateCmb.getSelectedItem());
    settings.setAutoscroll(checkBoxAutoscroll.isSelected());
    settings.setSendAsYouType(checkBoxSendAsType.isSelected());
    settings.setLineEnding(comboBoxLineEnding.getSelectedItem().toString());
    final SerialPortCmbItem selectedPort = (SerialPortCmbItem) serialPortsCmb.getSelectedItem();
    if (selectedPort != null) {
      settings.setPortName(selectedPort.getSerialPort().getSystemPortName());
    }
    SETTINGS.flushToFile();
  }

  private void initPorts(boolean shouldSetDefaultValue) {
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
    textFieldLineToSend.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (checkBoxSendAsType.isSelected()) {
          sendChar(e.getKeyChar());
          clearSendField();
        } else {
          if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            buttonSend.doClick();
          }
        }
      }
    });
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        onApplicationExit();
      }
    });
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

  private void sendEnteredText() {
    try {
      openedPort.getOutputStream().write(textFieldLineToSend.getText().concat(getNewLine()).getBytes());
      clearSendField();
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
}
