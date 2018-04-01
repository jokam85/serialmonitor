package com.djordjem.serialmonitor.gui;

import com.djordjem.serialmonitor.gui.utils.DialogUtils;
import com.djordjem.serialmonitor.serialport.SerialPortService;
import com.djordjem.serialmonitor.settings.CommandGroup;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class HistoryListClickListener extends MouseAdapter {

  private SerialMonitor serialMonitor;

  public HistoryListClickListener(SerialMonitor serialMonitor) {
    this.serialMonitor = serialMonitor;
  }

  @Override
  public void mouseClicked(MouseEvent evt) {
    JList list = (JList) evt.getSource();
    if (evt.getClickCount() == 2 && SerialPortService.INSTANCE.isPortOpen()) {
      int index = list.locationToIndex(evt.getPoint());
      serialMonitor.sendLine((String) list.getModel().getElementAt(index), true, false);
    }
  }

  public void mousePressed(MouseEvent e) {
    if (e.isPopupTrigger()) {
      doPop(e);
    }
    super.mousePressed(e);
  }

  public void mouseReleased(MouseEvent e) {
    if (e.isPopupTrigger()) {
      doPop(e);
    }
    super.mouseReleased(e);
  }

  private void doPop(MouseEvent e) {
    JList list = (JList) e.getSource();
    int row = list.locationToIndex(e.getPoint());
    list.setSelectedIndex(row);

    HistoryContextMenu menu = new HistoryContextMenu(serialMonitor, (String) list.getModel().getElementAt(row));
    menu.show(e.getComponent(), e.getX(), e.getY());
  }

  /**
   * Context menu for history list
   */
  class HistoryContextMenu extends JPopupMenu {

    private SerialMonitor serialMonitor;
    private String command;

    private JMenuItem sendMenuItem = new JMenuItem("Send");
    private JMenuItem editBeforeSendMenuItem = new JMenuItem("Edit before send");
    private JMenuItem sendWithoutLineSepMenuItem = new JMenuItem("Send without line separator");
    private JMenu saveAsShortcut = new JMenu("Save as shortcut");
    private JMenuItem newGroupMenuItem = new JMenuItem("New shortcut group...");

    HistoryContextMenu(SerialMonitor serialMonitor, String command) {
      this.serialMonitor = serialMonitor;
      this.command = command;
      setEnabledStateForMenuItems();
      initListeners();
      addItems();
    }

    private void addItems() {
      add(sendMenuItem);
      add(editBeforeSendMenuItem);
      add(sendWithoutLineSepMenuItem);
      add(saveAsShortcut);

      // Submenu with command groups
      serialMonitor.commandGroupsComboBoxModel.getAllItems().forEach(commandGroup -> {
        JMenuItem commandGroupMI = new JMenuItem(commandGroup.getName());
        saveAsShortcut.add(commandGroupMI);
        commandGroupMI.addActionListener(e -> {
          String groupName = ((JMenuItem) e.getSource()).getText();
          serialMonitor.commandGroupsComboBoxModel.getAllItems().forEach(commandGroup1 -> {
            if (commandGroup.getName().equals(groupName)) {
              if (!commandGroup.getCommands().contains(command)) {
                commandGroup.addCommand(command);
                serialMonitor.renderShortcutButtons();
              }
            }
          });
        });
      });
      saveAsShortcut.add(new JSeparator());
      saveAsShortcut.add(newGroupMenuItem);
      newGroupMenuItem.addActionListener(e -> addCommandToNewGroup());
    }

    private void setEnabledStateForMenuItems() {
      sendMenuItem.setEnabled(SerialPortService.INSTANCE.isPortOpen());
      editBeforeSendMenuItem.setEnabled(SerialPortService.INSTANCE.isPortOpen());
      sendWithoutLineSepMenuItem.setEnabled(SerialPortService.INSTANCE.isPortOpen());
    }

    private void initListeners() {
      sendMenuItem.addActionListener(e -> serialMonitor.sendLine(command, true, false));
      sendWithoutLineSepMenuItem.addActionListener(e -> serialMonitor.sendLine(command, false, false));
      editBeforeSendMenuItem.addActionListener(e -> {
        serialMonitor.textFieldLineToSend.setText(command);
        serialMonitor.textFieldLineToSend.grabFocus();
      });
    }

    private void addCommandToNewGroup() {
      String newGroupName = DialogUtils.textInput(this.serialMonitor, "Shortcut group name");
      CommandGroup newGroup = new CommandGroup(newGroupName);
      newGroup.addCommand(command);
      serialMonitor.commandGroupsComboBoxModel.addElement(newGroup);
      serialMonitor.commandGroupsComboBoxModel.setSelectedItem(newGroup);
    }
  }
}
