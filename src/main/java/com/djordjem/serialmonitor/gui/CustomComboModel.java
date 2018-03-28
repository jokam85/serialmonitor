package com.djordjem.serialmonitor.gui;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class CustomComboModel<T> extends DefaultComboBoxModel<T> {

  public List<T> getAllItems() {
    List<T> l = new ArrayList<>(getSize());
    int size = getSize();
    IntStream.range(0, size).forEach(i -> l.add(getElementAt(i)));
    return l;
  }

}
