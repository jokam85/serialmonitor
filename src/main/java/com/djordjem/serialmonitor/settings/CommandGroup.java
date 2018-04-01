package com.djordjem.serialmonitor.settings;

import java.util.ArrayList;
import java.util.List;

public class CommandGroup {

  private String name;

  private List<String> commands = new ArrayList<>();

  CommandGroup() {
    super();
  }

  public CommandGroup(String name) {
    this.name = name;
  }

  public CommandGroup addCommand(String command) {
    if (command != null && command.trim().length() > 0)
      commands.add(command);
    return this;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<String> getCommands() {
    return commands;
  }

  public void setCommands(List<String> commands) {
    this.commands = commands;
  }

  @Override
  public String toString() {
    return name;
  }
}
