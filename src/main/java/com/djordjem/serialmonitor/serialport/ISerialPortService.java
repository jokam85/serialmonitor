package com.djordjem.serialmonitor.serialport;

import java.util.List;

public interface ISerialPortService {

  List<SerialPortDTO> getPorts();

  void openPort(String sysPortName, int baudRate);

  void closePort();

  void addDataListener(SerialPortDataListener dataListener);

  void removeDataListener(SerialPortDataListener dataListener);

  void addPortsChangedListener(SerialPortsListListener dataListener);

  boolean isPortOpen();

  void sendLine(String line, String newLineSeparator);

  void sendChar(char c);
}
