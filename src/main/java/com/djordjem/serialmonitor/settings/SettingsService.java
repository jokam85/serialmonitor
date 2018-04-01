package com.djordjem.serialmonitor.settings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public enum SettingsService {
  SETTINGS;

  static final ObjectMapper objectMapper = new ObjectMapper();
  static final String USER_DIR = System.getProperty("user.home");
  static final String FILE_NAME = ".serialmonitor";

  static {
    objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
  }

  private Settings settings;

  public Settings getSettings() {
    if (this.settings == null) {
      this.settings = loadFromFile().orElse(SettingsFactory.createDefaultSettings());
    }
    return this.settings;
  }

  public void flushToFile() {
    try {
      objectMapper.writeValue(settingsFile(), this.settings);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static Optional<Settings> loadFromFile() {
    File sf = settingsFile();
    if (sf.exists() && settingsFile().canRead()) {
      try {
        return Optional.of(objectMapper.readValue(settingsFile(), Settings.class));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return Optional.empty();
  }

  private static File settingsFile() {
    return new File(USER_DIR.concat(File.separator).concat(FILE_NAME));
  }

}
