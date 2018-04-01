package com.djordjem.serialmonitor.gui.maindlg;

import com.djordjem.serialmonitor.gui.models.CustomComboModel;
import com.djordjem.serialmonitor.model.CommandGroup;

public class CommandGroupsComboModel extends CustomComboModel<CommandGroup> {

  public void setSelectedGroupByName(String groupName) {
    super.setSelectedItem(getAllItems().stream().filter(cg -> cg.getName().equals(groupName)).findFirst().orElse(null));
  }
}
