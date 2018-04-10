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

public class FazecastSerialPortService implements com.fazecast.jSerialComm.SerialPortDataListener, ISerialPortService {

  private List<SerialPortDataListener> dataListeners = new ArrayList<>();

  private List<SerialPortsListListener> serialPortListeners = new ArrayList<>();

  private SerialPort openedPort;

  private List<SerialPortDTO> previoslyDetectedSerialPorts = new ArrayList<>();

  public FazecastSerialPortService() {
    Timer serialPortChecker = new Timer(1000, (event) -> checkIfPortsChanged());
    serialPortChecker.start();
  }

  @Override
  public List<SerialPortDTO> getPorts() {

    return Stream.of(SerialPort.getCommPorts()).map(serialPort -> new SerialPortDTO(serialPort.getSystemPortName(), serialPort.getDescriptivePortName())).collect(Collectors.toList());
  }

  @Override
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

  @Override
  public void closePort() {
    if (openedPort != null && openedPort.isOpen()) {
      openedPort.removeDataListener();
      openedPort.closePort();
      openedPort = null;
    }
  }

  @Override
  public void addDataListener(SerialPortDataListener dataListener) {
    this.dataListeners.add(dataListener);
  }

  @Override
  public void addPortsChangedListener(SerialPortsListListener dataListener) {
    this.serialPortListeners.add(dataListener);
  }

  @Override
  public void removeDataListener(SerialPortDataListener dataListener) {
    this.dataListeners.remove(dataListener);
  }

  @Override
  public boolean isPortOpen() {
    return openedPort != null && openedPort.isOpen();
  }

  @Override
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

  @Override
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

  private void checkIfPortsChanged() {
    // No listeners? No check needed.
    if (serialPortListeners.isEmpty()) {
      return;
    }
    List<SerialPortDTO> ports = getPorts();
    if (!(previoslyDetectedSerialPorts.containsAll(ports) && previoslyDetectedSerialPorts.size() == ports.size())) {
      this.previoslyDetectedSerialPorts = ports;
      notifyListenersOnPortListChanged();
    }
  }

  private void notifyListenersOnNewData(byte[] data) {
    this.dataListeners.forEach(listener -> listener.onNewData(data));
  }

  private void notifyListenersOnPortListChanged() {
    this.serialPortListeners.forEach(listener -> listener.portListChanged(previoslyDetectedSerialPorts));
  }
}
