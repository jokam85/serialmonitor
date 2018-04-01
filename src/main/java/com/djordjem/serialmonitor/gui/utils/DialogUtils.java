package com.djordjem.serialmonitor.gui.utils;

import javax.swing.*;
import java.awt.*;

import static javax.swing.JOptionPane.YES_NO_OPTION;
import static javax.swing.JOptionPane.YES_OPTION;

public abstract class DialogUtils {

  public static boolean yesNo(Component owner, String text) {
    return YES_OPTION == JOptionPane.showConfirmDialog(owner, text, "Please confirm", YES_NO_OPTION);
  }

  public static boolean yesNo(Component owner, String title, String text) {
    return YES_OPTION == JOptionPane.showConfirmDialog(owner, text, title, YES_NO_OPTION);
  }

  public static String textInput(Component owner, String text) {
    return JOptionPane.showInputDialog(owner, text);
  }
}
