package com.djordjem.serialmonitor.gui;

import com.djordjem.serialmonitor.settings.CommandGroup;

public class CommandGroupsComboModel extends CustomComboModel<CommandGroup> {

  public void setSelectedGroupByName(String groupName) {
    super.setSelectedItem(getAllItems().stream().filter(cg -> cg.getName().equals(groupName)).findFirst().orElse(null));
  }
}
