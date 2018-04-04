package com.djordjem.serialmonitor.exc;

public class FileCouldNotBeSavedException extends RuntimeException {

  public FileCouldNotBeSavedException(Exception e) {
    super(e);
  }

}
