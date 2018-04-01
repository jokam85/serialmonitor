package com.djordjem.serialmonitor.gui.commanddlg;

import com.djordjem.serialmonitor.gui.models.CustomListModel;
import com.djordjem.serialmonitor.gui.utils.DialogUtils;
import com.djordjem.serialmonitor.model.CommandGroup;

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
    CommandListContextMenu menu = new CommandListContextMenu(list, (CommandGroup) list.getModel().getElementAt(row));
    menu.show(e.getComponent(), e.getX(), e.getY());
  }

  /**
   * Context menu for history list
   */
  class CommandListContextMenu extends JPopupMenu {

    private CommandGroup commandGroup;
    private JList cgList;

    private JMenuItem deleteMenuItem = new JMenuItem("Delete");
    private JMenuItem renameMenuItem = new JMenuItem("Rename");

    CommandListContextMenu(JList cgList, CommandGroup commandGroup) {
      this.commandGroup = commandGroup;
      this.cgList = cgList;
      initListeners();
      add(renameMenuItem);
      add(deleteMenuItem);
    }

    private void initListeners() {
      deleteMenuItem.addActionListener(e -> {
        if (DialogUtils.yesNo(owner, "Are you sure you want to delete command group?")) {
          groupListModel.removeElement(commandGroup);
        }
      });
      renameMenuItem.addActionListener(e -> {
        String newName = DialogUtils.textInput(owner, "New command group name", commandGroup.getName());
        if (newName != null && newName.trim().length() > 0) {
          commandGroup.setName(newName);
          cgList.updateUI();
        }
      });
    }
  }
}
