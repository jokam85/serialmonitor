package com.djordjem.serialmonitor;

import com.fazecast.jSerialComm.SerialPort;

public class SerialPortCmbItem {

  private SerialPort serialPort;

  public SerialPortCmbItem(SerialPort serialPort) {
    this.serialPort = serialPort;
  }

  public SerialPort getSerialPort() {
    return serialPort;
  }

  @Override
  public String toString() {
    return serialPort.getDescriptivePortName();
  }
}
