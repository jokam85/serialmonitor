package com.djordjem.serialmonitor;

import com.djordjem.serialmonitor.gui.SerialMonitor;
import com.djordjem.serialmonitor.settings.Settings;

import javax.swing.*;

import static com.djordjem.serialmonitor.settings.SettingsService.SETTINGS;

public class Main {

  public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    SerialMonitor dialog = new SerialMonitor();
    final Settings settings = SETTINGS.getSettings();
    dialog.pack();
    dialog.setSize(settings.getWidth(), settings.getHeight());
    dialog.setLocation(settings.getPosX(), settings.getPosY());
    dialog.setVisible(true);
    System.exit(0);
  }
}
