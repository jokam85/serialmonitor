package com.djordjem.serialmonitor.serialport;

public interface SerialPortEventListener {

  void onNewData(byte[] data);

}
