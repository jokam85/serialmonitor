package com.djordjem.serialmonitor.gui.commanddlg;

import com.djordjem.serialmonitor.gui.models.CustomListModel;
import com.djordjem.serialmonitor.gui.utils.DialogUtils;
import com.djordjem.serialmonitor.model.CommandGroup;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CommandListClickListener extends MouseAdapter {

  private JList<String> commandList;
  private CommandGroup cg;

  public CommandListClickListener(CommandGroup cg, JList<String> commandList) {
    this.commandList = commandList;
    this.cg = cg;
  }

  @Override
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
    CommandListContextMenu menu = new CommandListContextMenu(cg, (String) list.getModel().getElementAt(row));
    menu.show(e.getComponent(), e.getX(), e.getY());
  }

  /**
   * Context menu for history list
   */
  class CommandListContextMenu extends JPopupMenu {

    private String command;
    private CommandGroup commandGroup;

    private JMenuItem deleteMenuItem = new JMenuItem("Delete");

    CommandListContextMenu(CommandGroup commandGroup, String command) {
      this.command = command;
      this.commandGroup = commandGroup;
      initListeners();
      add(deleteMenuItem);
    }

    private void initListeners() {
      deleteMenuItem.addActionListener(e -> {
        if (DialogUtils.yesNo(commandList, "Are you sure you want to delete command?")) {
          ((CustomListModel) commandList.getModel()).removeElement(command);
          commandGroup.getCommands().remove(command);
        }
      });
    }
  }
}
