package com.djordjem.serialmonitor.serialport;

import com.djordjem.serialmonitor.exc.ErrorDuringReadException;
import com.djordjem.serialmonitor.exc.ErrorDuringWriteException;
import com.djordjem.serialmonitor.exc.PortCouldNotBeOpenedException;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum SerialPortService implements com.fazecast.jSerialComm.SerialPortDataListener {
  INSTANCE;

  private List<SerialPortDataListener> dataListeners = new ArrayList<>();

  private List<SerialPortsListener> serialPortListeners = new ArrayList<>();

  private SerialPort openedPort;

  private List<SerialPortDTO> previoslyDetectedSerialPorts = new ArrayList<>();

  SerialPortService() {
    Timer serialPortChecker = new Timer(1000, (event) -> {
      List<SerialPortDTO> ports = getPorts();
      if (!(previoslyDetectedSerialPorts.containsAll(ports) && previoslyDetectedSerialPorts.size() == ports.size())) {
        this.previoslyDetectedSerialPorts = ports;
        notifyListenersOnPortListChanged();
      }
    });
    serialPortChecker.start();
  }

  public List<SerialPortDTO> getPorts() {
    return Stream.of(SerialPort.getCommPorts()).map(SerialPortDTO::new).collect(Collectors.toList());
  }

  public void openPort(String sysPortName, int baudRate) {
    closePort();
    SerialPort portToOpen = SerialPort.getCommPort(sysPortName);
    if (portToOpen.getDescriptivePortName().equals("Bad Port")) {
      throw new PortCouldNotBeOpenedException();
    }
    portToOpen.setBaudRate(baudRate);
    if (!portToOpen.openPort()) {
      throw new PortCouldNotBeOpenedException();
    }
    openedPort = portToOpen;
    openedPort.addDataListener(this);
  }

  public void closePort() {
    if (openedPort != null && openedPort.isOpen()) {
      openedPort.removeDataListener();
      openedPort.closePort();
      openedPort = null;
    }
  }

  public void addDataListener(SerialPortDataListener dataListener) {
    this.dataListeners.add(dataListener);
  }

  public void addPortsChangedListener(SerialPortsListener dataListener) {
    this.serialPortListeners.add(dataListener);
  }

  public void removeDataListener(SerialPortDataListener dataListener) {
    this.dataListeners.remove(dataListener);
  }

  public boolean isPortOpen() {
    return openedPort != null && openedPort.isOpen();
  }

  public void sendLine(String line, String newLineSeparator) {
    try {
      openedPort.getOutputStream().write(line.getBytes());
      if (newLineSeparator != null) {
        openedPort.getOutputStream().write(newLineSeparator.getBytes());
      }
    } catch (IOException e1) {
      closePort();
      throw new ErrorDuringWriteException();
    }
  }

  public void sendChar(char c) {
    try {
      openedPort.getOutputStream().write(c);
    } catch (IOException e1) {
      closePort();
      throw new ErrorDuringWriteException();
    }
  }

  @Override
  public int getListeningEvents() {
    return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
  }

  @Override
  public void serialEvent(SerialPortEvent event) {
    if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
      return;
    }
    try {
      byte[] newData = new byte[openedPort.bytesAvailable()];
      int numRead = openedPort.readBytes(newData, newData.length);
      if (numRead > 0) {
        this.notifyListenersOnNewData(newData);
      }
    } catch (Exception e) {
      closePort();
      throw new ErrorDuringReadException();
    }
  }

  private void notifyListenersOnNewData(byte[] data) {
    this.dataListeners.forEach(listener -> listener.onNewData(data));
  }

  private void notifyListenersOnPortListChanged() {
    this.serialPortListeners.forEach(listener -> listener.portListChanged(previoslyDetectedSerialPorts));
  }

}
