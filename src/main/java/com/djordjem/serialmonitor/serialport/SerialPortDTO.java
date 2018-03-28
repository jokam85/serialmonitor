package com.djordjem.serialmonitor.serialport;

import com.fazecast.jSerialComm.SerialPort;

import java.util.Objects;

public class SerialPortDTO {

  private SerialPort serialPort;

  public SerialPortDTO(SerialPort serialPort) {
    this.serialPort = serialPort;
  }

  public SerialPort getSerialPort() {
    return serialPort;
  }

  public String getDescriptivePortName() {
    return serialPort.getDescriptivePortName();
  }

  public String getSystemPortName() {
    return serialPort.getSystemPortName();
  }

  @Override
  public String toString() {
    return serialPort.getDescriptivePortName();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SerialPortDTO that = (SerialPortDTO) o;
    return Objects.equals(serialPort.getDescriptivePortName(), that.serialPort.getDescriptivePortName());
  }

  @Override
  public int hashCode() {
    return Objects.hash(serialPort.getDescriptivePortName());
  }
}
