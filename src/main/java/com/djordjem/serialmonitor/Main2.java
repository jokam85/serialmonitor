package com.djordjem.serialmonitor;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

import java.util.Arrays;

public class Main2 {

  public static void main(String[] args) throws SerialPortException {


    Arrays.asList(SerialPortList.getPortNames()).forEach(sp -> System.out.println(sp));

    SerialPort sp = new SerialPort("com9");
    sp.openPort();
    System.out.println(sp.getPortName());
    sp.addEventListener(e -> {
      System.out.println(e.getEventValue());
    });
  }
}
