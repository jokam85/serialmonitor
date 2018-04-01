package com.djordjem.serialmonitor.gui.commanddlg;

import com.djordjem.serialmonitor.gui.models.CustomListModel;
import com.djordjem.serialmonitor.gui.utils.DialogUtils;
import com.djordjem.serialmonitor.model.CommandGroup;
import com.djordjem.serialmonitor.settings.SettingsFactory;
import com.djordjem.serialmonitor.settings.SettingsService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class CommandsEditDialog extends JDialog {

  private Timer t;

  private JPanel contentPane;
  private JButton buttonOK;
  private JButton buttonCancel;
  private JButton addCommandGroupButton;
  private JButton addCommandButton;
  private JList<CommandGroup> groupList;
  private JList<String> commandList;
  private JButton restoreDefaultsbutton;

  private CustomListModel<CommandGroup> groupListModel = new CustomListModel<>();
  private CustomListModel<String> commandModel = new CustomListModel<>();

  private boolean ok = false;

  public CommandsEditDialog(Dialog owner, List<CommandGroup> commandGroups) {
    super(owner);
    initDialog(owner);
    initListeners();
    initGuiUpdater();
    initGroupList(commandGroups);
  }

  public boolean isOk() {
    return ok;
  }

  private void initDialog(Dialog owner) {
    setContentPane(contentPane);
    setModal(true);
    setTitle("Edit commands");
    getRootPane().setDefaultButton(buttonOK);
    setSize(800, 500);
    setLocation(owner.getX() + (owner.getSize().width - getSize().width) / 2, owner.getY() + 60);
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
  }

  private void initListeners() {
    buttonOK.addActionListener(e -> onOK());
    buttonCancel.addActionListener(e -> onCancel());
    addCommandGroupButton.addActionListener(e -> addGroup());
    addCommandButton.addActionListener(e -> addCommand());
    restoreDefaultsbutton.addActionListener(e -> restoreDefaults());
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        onCancel();
      }
    });
  }

  private void initGuiUpdater() {
    t = new Timer(200, (e) -> updateGUIState());
    t.start();
  }

  private void initGroupList(List<CommandGroup> commandGroups) {
    groupList.setModel(groupListModel);
    commandGroups.forEach(cg -> groupListModel.insertElementAt(cg, groupListModel.getSize()));
    groupList.addMouseListener(new CommandGroupListClickListener(this, groupListModel));
    groupList.addListSelectionListener(e -> {
      if (!e.getValueIsAdjusting()) {
        commandModel = new CustomListModel<>();
        commandList.setModel(commandModel);
        CommandGroup cg = groupList.getSelectedValue();
        if (cg != null) {
          commandList.removeMouseListener(commandList.getMouseListeners()[0]);
          commandList.addMouseListener(new CommandListClickListener(cg, commandList));
          cg.getCommands().forEach(c -> commandModel.insertElementAt(c, commandModel.getSize()));
        }
      }
    });
  }

  private void addGroup() {
    String newGroupName = DialogUtils.textInput(this, "Command group name");
    CommandGroup newGroup = new CommandGroup(newGroupName);
    groupListModel.add(groupListModel.getSize(), newGroup);
  }

  private void addCommand() {
    CommandGroup cg = groupList.getSelectedValue();
    if (cg != null) {
      String command = DialogUtils.textInput(this, "Command");
      if (command != null && command.trim().length() > 0) {
        cg.addCommand(command);
        commandModel.add(commandModel.getSize(), command);
      }
    }
  }

  private void restoreDefaults() {
    if (DialogUtils.yesNo(this, "Are you sure you want to restore defaults and loose your custom groups and commands?")) {
      groupListModel.removeAllElements();
      SettingsFactory.createDefaultGroups().forEach(groupListModel::addToBottom);
    }
  }

  private void onOK() {
    ok = true;
    saveDataToSettings();
    dispose();
  }

  private void onCancel() {
    t.stop();
    dispose();
  }

  private void updateGUIState() {
    addCommandButton.setEnabled(groupList.getSelectedValue() != null);
  }

  private void saveDataToSettings() {
    SettingsService.SETTINGS.getSettings().setNewCommandGroups(groupListModel.getAllItems());
  }
}
