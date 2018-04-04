package com.djordjem.serialmonitor.gui.utils;

import com.djordjem.serialmonitor.exc.FileCouldNotBeSavedException;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class FileUtils {

  private FileUtils() {
  }

  public static File saveTextToFile(Component modalOwner, String content, File defaultFile) throws FileCouldNotBeSavedException {
    try {
      JFileChooser fileChooser = new JFileChooser();
      fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
      fileChooser.setSelectedFile(defaultFile);
      if (fileChooser.showSaveDialog(modalOwner) == JFileChooser.APPROVE_OPTION) {
        File file = fileChooser.getSelectedFile();
        Files.write(Paths.get(file.getAbsolutePath()), content.getBytes());
        return file;
      }
      return null;
    } catch (IOException e) {
      throw new FileCouldNotBeSavedException(e);
    }
  }
}

