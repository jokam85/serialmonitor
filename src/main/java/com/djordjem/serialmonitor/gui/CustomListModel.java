package com.djordjem.serialmonitor.gui;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class CustomListModel<T> extends DefaultListModel<T> {

  public void addToBottom(T item) {
    insertElementAt(item, getSize());
  }

  public void addToTop(T item) {
    insertElementAt(item, 0);
  }

  public void addToTop(T item, boolean ignoreIfSameAsLast, int maxNumberOfItems) {
    if (ignoreIfSameAsLast && sameAsLast(item)) {
      return;
    }
    insertElementAt(item, 0);
    while (size() > maxNumberOfItems) {
      removeElementAt(size() - 1);
    }
  }

  public List<T> getAllItems() {
    List<T> l = new ArrayList<>(getSize());
    int size = getSize();
    IntStream.range(0, size).forEach(i -> l.add(getElementAt(i)));
    return l;
  }

  private boolean sameAsLast(T item) {
    int size = getSize();
    if (size == 0) {
      return false;
    } else {
      return get(0).equals(item);
    }
  }
}
