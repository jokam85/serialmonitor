package com.djordjem.serialmonitor.gui.maindlg;

import com.djordjem.serialmonitor.gui.commanddlg.CommandsEditDialog;
import com.djordjem.serialmonitor.gui.models.CustomListModel;
import com.djordjem.serialmonitor.gui.utils.FileUtils;
import com.djordjem.serialmonitor.model.CommandGroup;
import com.djordjem.serialmonitor.serialport.SerialPortDTO;
import com.djordjem.serialmonitor.serialport.SerialPortDataListener;
import com.djordjem.serialmonitor.serialport.SerialPortService;
import com.djordjem.serialmonitor.settings.Settings;
import com.djordjem.serialmonitor.settings.SettingsService;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.stream.IntStream;

import static com.djordjem.serialmonitor.constants.Constants.POSSIBLE_BAUDRATES;
import static com.djordjem.serialmonitor.settings.SettingsService.SETTINGS;

public class MainDialog extends JDialog implements SerialPortDataListener {

  // Models
  private Settings settings;
  private CustomListModel<String> historyListModel;
  SerialPortComboModelList portsCmbModel;
  CommandGroupsComboModel commandGroupsComboBoxModel;

  JPanel contentPane;
  JComboBox<SerialPortDTO> serialPortsCmb;
  JButton openPortBtn;
  JComboBox<Integer> baudRateCmb;
  JButton closeBtn;
  JButton clearBtn;
  JTextArea serialText;
  JCheckBox checkBoxAutoscroll;
  JTextField sendTextField;
  JButton sendButton;
  JComboBox comboBoxLineEnding;
  JCheckBox checkBoxSendAsType;
  JPanel jPanelFooter;
  JList<String> historyList;
  JSplitPane historyTextSplit;
  JButton clearHistoryBtn;
  JComboBox<CommandGroup> commandGroupsComboBox;
  JPanel commandButtonContainerPanel;
  JButton editCommandBtn;
  JButton saveLogButton;
  JButton saveHistoryButton;

  private final GuiUpdater guiUpdater = new GuiUpdater(this);

  public MainDialog() {
    super(null, java.awt.Dialog.ModalityType.TOOLKIT_MODAL);

    guiUpdater.start();

    settings = SETTINGS.getSettings();

    portsCmbModel = new SerialPortComboModelList();
    serialPortsCmb.setModel(portsCmbModel);

    historyListModel = new CustomListModel<>();
    historyList.setModel(historyListModel);

    commandGroupsComboBoxModel = new CommandGroupsComboModel();
    commandGroupsComboBox.setModel(commandGroupsComboBoxModel);

    setContentPane(contentPane);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setTitle("Serial monitor");
    initRates();
    initListeners();
    applySettings();

    SerialPortService.INSTANCE.addDataListener(this);
  }

  @Override
  public void onNewData(byte[] newData) {
    String data = new String(newData);
    serialText.append(data);
    if (checkBoxAutoscroll.isSelected()) {
      serialText.setCaretPosition(serialText.getDocument().getLength());
    }
  }

  private void openPort() {
    SerialPortDTO selectedItem = (SerialPortDTO) serialPortsCmb.getSelectedItem();
    if (selectedItem != null) {
      String portName = selectedItem.getSystemPortName();
      Integer baudRate = (Integer) baudRateCmb.getSelectedItem();
      int baud = baudRate == null ? 9600 : baudRate;
      SerialPortService.INSTANCE.openPort(portName, baud);
    }
  }

  private void onApplicationExit() {
    guiUpdater.stop();
    SerialPortService.INSTANCE.closePort();
    SerialPortService.INSTANCE.removeDataListener(this);
    dispose();
    saveSettings();
  }

  private void initRates() {
    for (int rate : POSSIBLE_BAUDRATES) {
      baudRateCmb.addItem(rate);
    }
    baudRateCmb.setSelectedItem(settings.getBaudRate());
  }

  private void initListeners() {
    openPortBtn.addActionListener(e -> openPort());
    closeBtn.addActionListener(e -> SerialPortService.INSTANCE.closePort());
    clearBtn.addActionListener(e -> clearTextField());
    saveLogButton.addActionListener(e -> saveLogAs());
    sendButton.addActionListener(e -> sendEnteredText());
    clearHistoryBtn.addActionListener(e -> historyListModel.clear());
    editCommandBtn.addActionListener(e -> openCommandDialog());
    sendTextField.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (checkBoxSendAsType.isSelected()) {
          SerialPortService.INSTANCE.sendChar(e.getKeyChar());
        } else {
          if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            sendEnteredText();
          }
        }
      }

      @Override
      public void keyReleased(KeyEvent e) {
        if (checkBoxSendAsType.isSelected()) {
          clearSendField();
        }
      }
    });
    historyList.addMouseListener(new HistoryListClickListener(this));
    saveHistoryButton.addActionListener(e -> saveHistoryAs());

    commandGroupsComboBox.addActionListener(event -> renderCommandButtons());
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        onApplicationExit();
      }
    });
  }

  private void sendEnteredText() {
    sendLine(sendTextField.getText(), true, true);
    clearSendField();
  }

  void sendLine(String line, boolean includeLineSeparator, boolean addToHistory) {
    SerialPortService.INSTANCE.sendLine(line, includeLineSeparator ? getNewLine() : null);
    if (addToHistory) {
      historyListModel.addToTop(line, true, settings.getMaxHistoryEntries());
    }
  }

  private void clearSendField() {
    sendTextField.setText("");
  }

  private void clearTextField() {
    serialText.setText("");
  }

  private void saveLogAs() {
    Settings s = SettingsService.SETTINGS.getSettings();
    File lastFile = null;
    String filePath = s.getLastLogSaveAsFilePath();
    if (filePath != null) {
      lastFile = new File(filePath);
    }
    File savedFile = FileUtils.saveTextToFile(this, serialText.getText(), lastFile);
    if (savedFile != null) {
      s.setLastLogSaveAsFilePath(savedFile.getAbsolutePath());
    }
  }

  private void saveHistoryAs() {
    Settings s = SettingsService.SETTINGS.getSettings();
    String filePath = s.getLastHistorySaveAsFilePath();
    File lastFile = filePath != null ? new File(filePath) : null;
    File savedFile = FileUtils.saveTextToFile(this, String.join(System.lineSeparator(), historyListModel.getAllItems()), lastFile);
    if (savedFile != null) {
      s.setLastHistorySaveAsFilePath(savedFile.getAbsolutePath());
    }
  }

  private String getNewLine() {
    String nlSeparator = (String) comboBoxLineEnding.getSelectedItem();
    if (nlSeparator == null) {
      return "";
    }
    if (nlSeparator.equals("\\n")) {
      return "\n";
    }
    if (nlSeparator.equals("\\n")) {
      return "\r";
    }
    if (nlSeparator.equals("\\r\\n")) {
      return "\r\n";
    }
    return "";
  }

  private void applySettings() {
    settings.getHistory().forEach(historyListModel::addElement);
    checkBoxAutoscroll.setSelected(settings.getAutoscroll());
    checkBoxSendAsType.setSelected(settings.getSendAsYouType());
    comboBoxLineEnding.setSelectedItem(settings.getLineEnding());
    historyTextSplit.setDividerLocation(settings.getHistoryTextSeparatorPosition());

    reloadGroupsFromSettings();
    commandGroupsComboBoxModel.setSelectedGroupByName(settings.getCommandGroupName());
  }

  private void saveSettings() {
    settings.setPosX(getX());
    settings.setPosY(getY());
    settings.setWidth(getWidth());
    settings.setHeight(getHeight());
    settings.setBaudRate((Integer) baudRateCmb.getSelectedItem());
    settings.setAutoscroll(checkBoxAutoscroll.isSelected());
    settings.setSendAsYouType(checkBoxSendAsType.isSelected());
    settings.setLineEnding((String) comboBoxLineEnding.getSelectedItem());
    settings.setHistoryTextSeparatorPosition(historyTextSplit.getDividerLocation());

    // Copy history to settings object
    int historySize = historyListModel.getSize();
    settings.getHistory().clear();
    IntStream.range(0, historySize).forEach(i -> settings.getHistory().add(historyListModel.getElementAt(i)));

    // Selected port
    final SerialPortDTO selectedPort = (SerialPortDTO) serialPortsCmb.getSelectedItem();
    if (selectedPort != null) {
      settings.setPortName(selectedPort.getSystemPortName());
    }

    // Groups
    CommandGroup selectedGroup = (CommandGroup) commandGroupsComboBoxModel.getSelectedItem();
    if (selectedGroup != null) {
      settings.setCommandGroupName(selectedGroup.getName());
    }
    settings.setNewCommandGroups(commandGroupsComboBoxModel.getAllItems());
    SETTINGS.flushToFile();
  }

  private void reloadGroupsFromSettings() {
    CommandGroup prevSelItem = (CommandGroup) commandGroupsComboBoxModel.getSelectedItem();
    commandGroupsComboBoxModel.removeAllElements();
    settings.getGroups().forEach(commandGroupsComboBoxModel::addElement);
    if (prevSelItem != null && commandGroupsComboBoxModel.getIndexOf(prevSelItem) > -1) {
      commandGroupsComboBoxModel.setSelectedItem(prevSelItem);
    }
    renderCommandButtons();
  }

  void renderCommandButtons() {
    CommandGroup cg = (CommandGroup) commandGroupsComboBoxModel.getSelectedItem();
    commandButtonContainerPanel.removeAll();
    if (cg != null) {
      boolean portOpen = SerialPortService.INSTANCE.isPortOpen();
      cg.getCommands().forEach(cmd -> {
        JButton cmdBtn = new JButton(cmd.getCommand());
        cmdBtn.setEnabled(portOpen);
        commandButtonContainerPanel.add(cmdBtn);
        cmdBtn.addActionListener(e -> sendLine(cmd.getCommand(), true, true));
      });
    }
    commandButtonContainerPanel.updateUI();
  }

  private void openCommandDialog() {
    CommandsEditDialog sd = new CommandsEditDialog(this, this.commandGroupsComboBoxModel.getAllItems());
    sd.setVisible(true);
    if (sd.isOk()) {
      reloadGroupsFromSettings();
    }
  }
}
