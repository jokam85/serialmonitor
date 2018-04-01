package com.djordjem.serialmonitor.gui.commanddlg;

import com.djordjem.serialmonitor.gui.models.CustomListModel;
import com.djordjem.serialmonitor.gui.utils.DialogUtils;
import com.djordjem.serialmonitor.model.Command;
import com.djordjem.serialmonitor.model.CommandGroup;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CommandListClickListener extends MouseAdapter {

  private JList<Command> commandList;
  private CommandGroup cg;

  public CommandListClickListener(CommandGroup cg, JList<Command> commandList) {
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
    CommandListContextMenu menu = new CommandListContextMenu(list, cg, (Command) list.getModel().getElementAt(row));
    menu.show(e.getComponent(), e.getX(), e.getY());
  }

  /**
   * Context menu for history list
   */
  class CommandListContextMenu extends JPopupMenu {

    private Command command;
    private CommandGroup commandGroup;
    private JList<Command> cgList;

    private JMenuItem deleteMenuItem = new JMenuItem("Delete");
    private JMenuItem renameMenuItem = new JMenuItem("Rename");

    CommandListContextMenu(JList<Command> cgList, CommandGroup commandGroup, Command command) {
      this.cgList = cgList;
      this.commandGroup = commandGroup;
      this.command = command;
      initListeners();
      add(renameMenuItem);
      add(deleteMenuItem);
    }

    private void initListeners() {
      deleteMenuItem.addActionListener(e -> {
        if (DialogUtils.yesNo(commandList, "Are you sure you want to delete command?")) {
          ((CustomListModel<Command>) commandList.getModel()).removeElement(command);
          commandGroup.getCommands().remove(command);
        }
      });

      renameMenuItem.addActionListener(e -> {
        String newCommand = DialogUtils.textInput(commandList, "New command", command.getCommand());
        if (newCommand != null && newCommand.trim().length() > 0) {
          command.setCommand(newCommand);
          cgList.updateUI();
        }
      });
    }
  }
}
