package com.djordjem.serialmonitor.settings;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public enum SettingsService {
  SETTINGS;

  static final ObjectMapper objectMapper = new ObjectMapper();

  static final String USER_DIR = System.getProperty("user.home");
  static final String FILE_NAME = ".serialmonitor";

  private Settings settings;

  public Settings getSettings() {
    if (this.settings != null) {
      return this.settings;
    }
    this.settings = loadFromFile().orElse(new Settings());
    return this.settings;
  }

  public void flushToFile() {
    try {
      objectMapper.writeValue(settingsFile(), this.settings);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private Optional<Settings> loadFromFile() {
    try {
      return Optional.of(objectMapper.readValue(settingsFile(), Settings.class));
    } catch (IOException e) {
      e.printStackTrace();
    }
    return Optional.empty();
  }

  private File settingsFile() {
    return new File(USER_DIR.concat(File.separator).concat(FILE_NAME));
  }
}
