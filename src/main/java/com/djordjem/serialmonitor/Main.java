package com.djordjem.serialmonitor;

import com.djordjem.serialmonitor.gui.maindlg.MainDialog;
import com.djordjem.serialmonitor.serialport.ISerialPortService;
import com.djordjem.serialmonitor.serialport.JsscSerialPortService;
import com.djordjem.serialmonitor.settings.Settings;

import javax.swing.*;

import static com.djordjem.serialmonitor.settings.SettingsService.SETTINGS;

public class Main {

  //  public static final ISerialPortService SERIAL_PORT_SERVICE = new FazecastSerialPortService();
  public static final ISerialPortService SERIAL_PORT_SERVICE = new JsscSerialPortService();

  public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    MainDialog dialog = new MainDialog();
    final Settings settings = SETTINGS.getSettings();
    dialog.pack();
    dialog.setSize(settings.getWidth(), settings.getHeight());
    dialog.setLocation(settings.getPosX(), settings.getPosY());
    dialog.setVisible(true);
    System.exit(0);
  }
}
