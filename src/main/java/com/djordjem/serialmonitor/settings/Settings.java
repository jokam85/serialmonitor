package com.djordjem.serialmonitor.settings;

public class Settings {

  private Integer width = 800;
  private Integer height = 600;
  private Integer posX = 100;
  private Integer posY = 100;
  private Integer baudRate = 9600;
  private String portName;
  private Boolean autoscroll = false;

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
}
