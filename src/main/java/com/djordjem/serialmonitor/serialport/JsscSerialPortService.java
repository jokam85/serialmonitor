package com.djordjem.serialmonitor.serialport;

import com.djordjem.serialmonitor.exc.ErrorDuringReadException;
import com.djordjem.serialmonitor.exc.ErrorDuringWriteException;
import com.djordjem.serialmonitor.exc.PortCouldNotBeOpenedException;
import jssc.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JsscSerialPortService implements ISerialPortService, SerialPortEventListener {

  private List<SerialPortDataListener> dataListeners = new ArrayList<>();

  private List<SerialPortsListListener> serialPortListeners = new ArrayList<>();

  private SerialPort openedPort;

  private List<SerialPortDTO> previoslyDetectedSerialPorts = new ArrayList<>();

  public JsscSerialPortService() {
    Timer serialPortChecker = new Timer(1000, (event) -> checkIfPortsChanged());
    serialPortChecker.start();
  }

  @Override
  public List<SerialPortDTO> getPorts() {

    return Stream.of(SerialPortList.getPortNames()).map(serialPort -> new SerialPortDTO(serialPort, serialPort)).collect(Collectors.toList());
  }

  @Override
  public void openPort(String sysPortName, int baudRate) {
    closePort();
    SerialPort portToOpen = new SerialPort(sysPortName);
    try {
      portToOpen.openPort();
      portToOpen.setParams(baudRate, 8, 1, 0);
      portToOpen.setEventsMask(SerialPort.MASK_RXCHAR);
      portToOpen.addEventListener(this);
      openedPort = portToOpen;
    } catch (SerialPortException e) {
      e.printStackTrace();
      throw new PortCouldNotBeOpenedException();
    }
  }


  @Override
  public void serialEvent(SerialPortEvent event) {
    if (event.isRXCHAR()) {//If data is available
      int size = event.getEventValue();
      if (size > 0) {
        try {
          byte buffer[] = openedPort.readBytes(size);
          notifyListenersOnNewData(buffer);
        } catch (SerialPortException ex) {
          closePort();
          throw new ErrorDuringReadException();
        }
      }
    }
  }

  @Override
  public void closePort() {
    if (openedPort != null && openedPort.isOpened()) {
      try {
        openedPort.removeEventListener();
        openedPort.closePort();
        openedPort = null;
      } catch (SerialPortException e) {
        // TODO handle gracefully
        e.printStackTrace();
      }
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
    return openedPort != null && openedPort.isOpened();
  }

  @Override
  public void sendLine(String line, String newLineSeparator) {
    try {
      openedPort.writeBytes(line.getBytes());
      if (newLineSeparator != null) {
        openedPort.writeBytes(newLineSeparator.getBytes());
      }
    } catch (SerialPortException e) {
      closePort();
      throw new ErrorDuringWriteException();
    }
  }

  @Override
  public void sendChar(char c) {
    try {
      openedPort.writeBytes(String.valueOf(c).getBytes());
    } catch (SerialPortException e1) {
      closePort();
      throw new ErrorDuringWriteException();
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
