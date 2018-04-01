package com.djordjem.serialmonitor.model;

import java.util.ArrayList;
import java.util.List;

public class CommandGroup {

  private String name;

  private List<Command> commands = new ArrayList<>();

  CommandGroup() {
    super();
  }

  public CommandGroup(String name) {
    this.name = name;
  }

  public CommandGroup addCommand(Command command) {
    String cName = command.getCommand();
    if (cName != null && cName.trim().length() > 0) {
      commands.add(command);
    }
    return this;
  }

  public CommandGroup addCommand(String cName) {
    commands.add(new Command(cName));
    return this;
  }

  public void removeCommand(Command command) {
    commands.remove(command);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<Command> getCommands() {
    return commands;
  }

  @Override
  public String toString() {
    return name;
  }


}
