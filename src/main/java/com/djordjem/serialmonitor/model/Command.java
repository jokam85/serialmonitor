package com.djordjem.serialmonitor.model;

import java.util.Objects;

public class Command {

  private String command;

  public Command() {
    super();
  }

  public Command(String command) {
    this.command = command;
  }

  public String getCommand() {
    return command;
  }

  public void setCommand(String command) {
    this.command = command;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Command command1 = (Command) o;
    return Objects.equals(command, command1.command);
  }

  @Override
  public int hashCode() {

    return Objects.hash(command);
  }

  @Override
  public String toString() {
    return command;
  }
}
