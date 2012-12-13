package system;

import javax.swing.UIManager;

import ui.ConsolePanel;
import ui.MainFrame;

public class Main {

  public static void main(String[] args) {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
      ConsolePanel.get().err("Using garbage look and feel.");
    };
    MainFrame mf = MainFrame.get();
    mf.setVisible(true);
  }
}
