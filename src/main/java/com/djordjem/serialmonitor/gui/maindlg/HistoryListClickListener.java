package com.djordjem.serialmonitor.gui.maindlg;

import com.djordjem.serialmonitor.Main;
import com.djordjem.serialmonitor.gui.utils.DialogUtils;
import com.djordjem.serialmonitor.model.Command;
import com.djordjem.serialmonitor.model.CommandGroup;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class HistoryListClickListener extends MouseAdapter {

  private MainDialog mainDialog;

  public HistoryListClickListener(MainDialog mainDialog) {
    this.mainDialog = mainDialog;
  }

  @Override
  public void mouseClicked(MouseEvent evt) {
    JList list = (JList) evt.getSource();
    if (evt.getClickCount() == 2 && Main.SERIAL_PORT_SERVICE.isPortOpen()) {
      int index = list.locationToIndex(evt.getPoint());
      String c = (String) list.getModel().getElementAt(index);
      mainDialog.sendLine(c, true, false);
    }
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

    HistoryContextMenu menu = new HistoryContextMenu(mainDialog, (String) list.getModel().getElementAt(row));
    menu.show(e.getComponent(), e.getX(), e.getY());
  }

  /**
   * Context menu for history list
   */
  class HistoryContextMenu extends JPopupMenu {

    private MainDialog mainDialog;
    private String command;

    private JMenuItem sendMenuItem = new JMenuItem("Send");
    private JMenuItem editBeforeSendMenuItem = new JMenuItem("Edit before send");
    private JMenuItem sendWithoutLineSepMenuItem = new JMenuItem("Send without line separator");
    private JMenu saveCommandMenu = new JMenu("Save command");
    private JMenuItem newGroupMenuItem = new JMenuItem("New command group...");

    HistoryContextMenu(MainDialog mainDialog, String command) {
      this.mainDialog = mainDialog;
      this.command = command;
      setEnabledStateForMenuItems();
      initListeners();
      addItems();
    }

    private void addItems() {
      add(sendMenuItem);
      add(editBeforeSendMenuItem);
      add(sendWithoutLineSepMenuItem);
      add(saveCommandMenu);

      // Submenu with command groups
      CommandGroupsComboModel commandGroupsModel = mainDialog.commandGroupsComboBoxModel;
      commandGroupsModel.getAllItems().forEach(commandGroup -> {
        JMenuItem commandGroupMI = new JMenuItem(commandGroup.getName());
        saveCommandMenu.add(commandGroupMI);
        commandGroupMI.addActionListener(e -> addCommandToExistingGroup(commandGroup));
      });
      saveCommandMenu.add(new JSeparator());
      saveCommandMenu.add(newGroupMenuItem);
      newGroupMenuItem.addActionListener(e -> addCommandToNewGroup());
    }

    private void setEnabledStateForMenuItems() {
      sendMenuItem.setEnabled(Main.SERIAL_PORT_SERVICE.isPortOpen());
      editBeforeSendMenuItem.setEnabled(Main.SERIAL_PORT_SERVICE.isPortOpen());
      sendWithoutLineSepMenuItem.setEnabled(Main.SERIAL_PORT_SERVICE.isPortOpen());
    }

    private void initListeners() {
      sendMenuItem.addActionListener(e -> mainDialog.sendLine(command, true, false));
      sendWithoutLineSepMenuItem.addActionListener(e -> mainDialog.sendLine(command, false, false));
      editBeforeSendMenuItem.addActionListener(e -> {
        mainDialog.sendTextField.setText(command);
        mainDialog.sendTextField.grabFocus();
      });
    }

    private void addCommandToNewGroup() {
      String newGroupName = DialogUtils.textInput(this.mainDialog, "Command group name");
      CommandGroup newGroup = new CommandGroup(newGroupName);
      newGroup.addCommand(command);
      mainDialog.commandGroupsComboBoxModel.addElement(newGroup);
      mainDialog.commandGroupsComboBoxModel.setSelectedItem(newGroup);
    }

    private void addCommandToExistingGroup(CommandGroup commandGroup) {
      Command c = new Command(command);
      if (!commandGroup.getCommands().contains(c)) {
        commandGroup.addCommand(c);
        mainDialog.renderCommandButtons();
      }
    }
  }
}
