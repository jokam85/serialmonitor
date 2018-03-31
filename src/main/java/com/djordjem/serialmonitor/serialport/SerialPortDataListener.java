package com.djordjem.serialmonitor.serialport;

public interface SerialPortDataListener {

  void onNewData(byte[] data);

}
