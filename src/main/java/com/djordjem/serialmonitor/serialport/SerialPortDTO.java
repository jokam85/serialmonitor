package com.djordjem.serialmonitor.serialport;

import java.util.Objects;

public class SerialPortDTO {

  private String name;

  private String descriptiveName;

  public SerialPortDTO(String name, String descriptiveName) {
    this.name = name;
    this.descriptiveName = descriptiveName;
  }

  public SerialPortDTO(String name) {
    this.name = name;
    this.descriptiveName = name;
  }

  public String getName() {
    return name;
  }

  public String getDescriptiveName() {
    return descriptiveName;
  }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SerialPortDTO that = (SerialPortDTO) o;
    return Objects.equals(getName(), that.getName());
  }

  @Override
  public int hashCode() {
    return Objects.hash(descriptiveName);
  }
}
