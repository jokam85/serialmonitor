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
            .addCommand("AT2")
            .addCommand("AT3")
            .addCommand("AT4"));

    list.add(new CommandGroup("HC-12 433Mhz radio module")
            .addCommand("AT2")
            .addCommand("AT3")
            .addCommand("AT4"));

    list.add(new CommandGroup("HM-10 BLE Bluetooth 4.0")
            .addCommand("AT2")
            .addCommand("AT3")
            .addCommand("AT4"));

    list.add(new CommandGroup("SIM800L GPRS Module")
            .addCommand("AT2")
            .addCommand("AT3")
            .addCommand("AT4"));

    return list;
  }

}
