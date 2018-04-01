package com.djordjem.serialmonitor.settings;

import com.djordjem.serialmonitor.model.CommandGroup;

import java.util.ArrayList;
import java.util.List;

public abstract class SettingsFactory {

  public static Settings createDefaultSettings() {
    Settings s = new Settings();
    s.setNewCommandGroups(createDefaultGroups());
    return s;
  }

  public static List<CommandGroup> createDefaultGroups() {
    ArrayList<CommandGroup> list = new ArrayList<>();
    list.add(new CommandGroup("HC-05 Bluetooth")
            .addCommand("AT")
            .addCommand("ATE")
            .addCommand("AT+GMR")
            .addCommand("AT+RST")
            .addCommand("AT+ADDR?"));

    return list;
  }

}
