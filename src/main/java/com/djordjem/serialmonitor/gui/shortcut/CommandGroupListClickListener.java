package com.djordjem.serialmonitor.gui.shortcut;

import com.djordjem.serialmonitor.gui.CustomListModel;
import com.djordjem.serialmonitor.gui.utils.DialogUtils;
import com.djordjem.serialmonitor.settings.CommandGroup;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CommandGroupListClickListener extends MouseAdapter {

  private CustomListModel<CommandGroup> groupListModel;

  private Component owner;

  public CommandGroupListClickListener(Component owner, CustomListModel<CommandGroup> groupListModel) {
    this.groupListModel = groupListModel;
    this.owner = owner;
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
    CommandListContextMenu menu = new CommandListContextMenu((CommandGroup) list.getModel().getElementAt(row));
    menu.show(e.getComponent(), e.getX(), e.getY());
  }

  /**
   * Context menu for history list
   */
  class CommandListContextMenu extends JPopupMenu {

    private CommandGroup commandGroup;

    private JMenuItem deleteMenuItem = new JMenuItem("Delete");

    CommandListContextMenu(CommandGroup commandGroup) {
      this.commandGroup = commandGroup;
      initListeners();
      add(deleteMenuItem);
    }

    private void initListeners() {
      deleteMenuItem.addActionListener(e -> {
        if (DialogUtils.yesNo(owner, "Are you sure you want to delete command group?")) {
          groupListModel.removeElement(commandGroup);
        }
      });
    }
  }
}
