package com.djordjem.serialmonitor.gui;

import com.djordjem.serialmonitor.settings.Settings;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
  private Settings settings;

  private Thread interfaceUpdateThread = new Thread(new Runnable() {
    public void run() {
      while (!Thread.interrupted()) {
        initPorts(false);
        if (openedPort != null) {
          openPortBtn.setEnabled(!openedPort.isOpen());
          closeButton.setEnabled(openedPort.isOpen());
          baudRateCmb.setEnabled(!openedPort.isOpen());
          serialPortsCmb.setEnabled(!openedPort.isOpen());
        } else {
          openPortBtn.setEnabled(true);
          closeButton.setEnabled(false);
          baudRateCmb.setEnabled(true);
          serialPortsCmb.setEnabled(true);
        }
        try {
          Thread.sleep(200);
        } catch (InterruptedException e) {
        }
      }
    }
  });

  private SerialPortDataListener dataListener = new SerialPortDataListener() {
    public int getListeningEvents() {
      return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
    }

    public void serialEvent(SerialPortEvent event) {
      if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE)
        return;
      byte[] newData = new byte[openedPort.bytesAvailable()];
      int numRead = openedPort.readBytes(newData, newData.length);
      if (numRead > 0) {
        String data = new String(newData);
        serialText.append(data);
        if (checkBoxAutoscroll.isSelected()) {
          serialText.setCaretPosition(serialText.getDocument().getLength());
        }
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

    interfaceUpdateThread.setPriority(Thread.NORM_PRIORITY - 1);
    interfaceUpdateThread.start();
  }


  private void onCancel() {
    interfaceUpdateThread.interrupt();
    dispose();
    settings.setPosX(getX());
    settings.setPosY(getY());
    settings.setWidth(getWidth());
    settings.setHeight(getHeight());
    settings.setBaudRate((Integer) baudRateCmb.getSelectedItem());
    settings.setAutoscroll(checkBoxAutoscroll.isSelected());
    final SerialPortCmbItem selectedPort = (SerialPortCmbItem) serialPortsCmb.getSelectedItem();
    if (selectedPort != null) {
      settings.setPortName(selectedPort.getSerialPort().getSystemPortName());
    }
    SETTINGS.flushToFile();
  }

  private void initPorts(boolean shouldSetDefaultValue) {
    if (needsReloadingPortList()) {
      this.ports = Arrays.asList(SerialPort.getCommPorts());
      ;
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

  private void initListeners() {
    openPortBtn.addActionListener(e -> openPort());
    closeButton.addActionListener(e -> closePort());
    clearButton.addActionListener(e -> serialText.setText(""));
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        onCancel();
      }
    });
  }
}
