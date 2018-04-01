package com.djordjem.serialmonitor.gui.shortcut;

import com.djordjem.serialmonitor.gui.CustomListModel;
import com.djordjem.serialmonitor.gui.utils.DialogUtils;
import com.djordjem.serialmonitor.settings.CommandGroup;
import com.djordjem.serialmonitor.settings.Settings;
import com.djordjem.serialmonitor.settings.SettingsService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class ShortcutsDialog extends JDialog {

  private Timer t;

  private JPanel contentPane;
  private JButton buttonOK;
  private JButton buttonCancel;
  private JButton addShortcutGroupButton;
  private JButton addShortcutButton;
  private JList<CommandGroup> groupList;
  private JList<String> shortcutList;
  private JButton restoreDefaultsbutton;

  private CustomListModel<CommandGroup> groupListModel = new CustomListModel<>();
  private CustomListModel<String> commandModel = new CustomListModel<>();

  private boolean ok = false;

  public ShortcutsDialog(Dialog owner, List<CommandGroup> commandGroups) {
    super(owner);
    initDialog(owner);
    initListeners();
    initGuiUpdater();
    initGroupList(commandGroups);
  }

  private void initDialog(Dialog owner) {
    setContentPane(contentPane);
    setModal(true);
    setTitle("Edit shortcuts");
    getRootPane().setDefaultButton(buttonOK);
    setSize(800, 500);
    setLocation(owner.getX() + (owner.getSize().width - getSize().width) / 2, owner.getY() + 60);
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
  }

  private void initListeners() {
    buttonOK.addActionListener(e -> onOK());
    buttonCancel.addActionListener(e -> onCancel());
    addShortcutGroupButton.addActionListener(e -> addGroup());
    addShortcutButton.addActionListener(e -> addShortcut());
    restoreDefaultsbutton.addActionListener(e -> restoreDefaults());
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        onCancel();
      }
    });
  }

  private void initGuiUpdater() {
    t = new Timer(200, (e) -> {
      addShortcutButton.setEnabled(groupList.getSelectedValue() != null);
    });
    t.start();
  }

  private void initGroupList(List<CommandGroup> commandGroups) {
    groupList.setModel(groupListModel);
    commandGroups.forEach(cg -> groupListModel.insertElementAt(cg, groupListModel.getSize()));
    groupList.addMouseListener(new CommandGroupListClickListener(this, groupListModel));
    groupList.addListSelectionListener(e -> {
      if (!e.getValueIsAdjusting()) {
        commandModel = new CustomListModel<>();
        shortcutList.setModel(commandModel);
        CommandGroup cg = groupList.getSelectedValue();
        if (cg != null) {
          shortcutList.removeMouseListener(shortcutList.getMouseListeners()[0]);
          shortcutList.addMouseListener(new CommandListClickListener(cg, shortcutList));
          cg.getCommands().forEach(c -> commandModel.insertElementAt(c, commandModel.getSize()));
        }
      }
    });
  }

  private void addGroup() {
    String newGroupName = JOptionPane.showInputDialog(this, "Shortcut group name");
    CommandGroup newGroup = new CommandGroup(newGroupName);
    groupListModel.add(groupListModel.getSize(), newGroup);
  }

  private void addShortcut() {
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
      Settings.createDefaultGroups().forEach(groupListModel::addToTop);
    }
  }

  private void onOK() {
    // add your code here
    ok = true;
    Settings settings = SettingsService.SETTINGS.getSettings();
    settings.getGroups().clear();
    groupListModel.getAllItems().forEach(settings::addGroup);
    dispose();
  }

  private void onCancel() {
    t.stop();
    dispose();
  }

  public boolean isOk() {
    return ok;
  }
}
