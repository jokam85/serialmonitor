package com.djordjem.serialmonitor.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Settings {

  private Integer maxHistoryEntries = 50;
  private Integer width = 800;
  private Integer height = 600;
  private Integer posX = 100;
  private Integer posY = 100;
  private Integer baudRate = 9600;
  private String portName;
  private String commandGroupName;
  private Boolean autoscroll = true;
  private Boolean sendAsYouType = false;
  private String lineEnding = "NL";
  private Integer historyTextSeparatorPosition = 100;
  private History history = new History();
  private Map<String, CommandGroup> commandGroups = new HashMap<>();


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

  Settings() {
    createDefaultGroups().forEach(this::addGroup);
  }

  public Map<String, CommandGroup> getGroups() {
    return commandGroups;
  }

  public void addGroup(CommandGroup group) {
    commandGroups.put(group.getName(), group);
  }

  public Integer getWidth() {
    return width;
  }

  public void setWidth(Integer width) {
    this.width = width;
  }

  public Integer getHeight() {
    return height;
  }

  public void setHeight(Integer height) {
    this.height = height;
  }

  public Integer getPosX() {
    return posX;
  }

  public void setPosX(Integer posX) {
    this.posX = posX;
  }

  public Integer getPosY() {
    return posY;
  }

  public void setPosY(Integer posY) {
    this.posY = posY;
  }

  public Integer getBaudRate() {
    return baudRate;
  }

  public void setBaudRate(Integer baudRate) {
    this.baudRate = baudRate;
  }

  public String getPortName() {
    return portName;
  }

  public void setPortName(String portName) {
    this.portName = portName;
  }

  public Boolean getAutoscroll() {
    return autoscroll;
  }

  public void setAutoscroll(Boolean autoscroll) {
    this.autoscroll = autoscroll;
  }

  public String getLineEnding() {
    return lineEnding;
  }

  public void setLineEnding(String lineEnding) {
    this.lineEnding = lineEnding;
  }

  public Boolean getSendAsYouType() {
    return sendAsYouType;
  }

  public void setSendAsYouType(Boolean sendAsYouType) {
    this.sendAsYouType = sendAsYouType;
  }

  public Integer getHistoryTextSeparatorPosition() {
    return historyTextSeparatorPosition;
  }

  public void setHistoryTextSeparatorPosition(Integer historyTextSeparatorPosition) {
    this.historyTextSeparatorPosition = historyTextSeparatorPosition;
  }

  public History getHistory() {
    return history;
  }

  public void setHistory(History history) {
    this.history = history;
  }

  public Integer getMaxHistoryEntries() {
    return maxHistoryEntries;
  }

  public void setMaxHistoryEntries(Integer maxHistoryEntries) {
    this.maxHistoryEntries = maxHistoryEntries;
  }

  public String getCommandGroupName() {
    return commandGroupName;
  }

  public void setCommandGroupName(String commandGroupName) {
    this.commandGroupName = commandGroupName;
  }
}
