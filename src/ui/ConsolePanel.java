package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ConsolePanel extends JPanel {
  private static final long serialVersionUID = 1L;

  private static ConsolePanel instance;

  public static ConsolePanel get() {
    if (instance == null) {
      instance = new ConsolePanel();
    }
    return instance;
  }

  private ConsolePanel() {
    setBackground(MainFrame.DARK_BLUE);
    setLayout(new BorderLayout());
    setPreferredSize(new Dimension(0, 200));

    add(getConsoleScrollPane());
  }

  private JScrollPane consoleScrollPane;

  private JScrollPane getConsoleScrollPane() {
    if (consoleScrollPane == null) {
      consoleScrollPane = new JScrollPane(getConsoleTextArea());
      consoleScrollPane.setBackground(null);
    }
    return consoleScrollPane;
  }

  private JTextArea consoleTextArea;

  private JTextArea getConsoleTextArea() {
    if (consoleTextArea == null) {
      consoleTextArea = new JTextArea();
      consoleTextArea.setEditable(false);
      consoleTextArea.setBackground(MainFrame.DARK_BLUE);
      consoleTextArea.setForeground(Color.WHITE);
      consoleTextArea.setFont(MainFrame.DEFAULT_FONT);
      consoleTextArea.setLineWrap(true);
      consoleTextArea.setWrapStyleWord(true);
    }
    return consoleTextArea;
  }

  public void clear() {
    getConsoleTextArea().setText("");
  }

  public void out(String text) {
    // System.out.println(text);
    getConsoleTextArea().append(text + "\r\n");
    getConsoleTextArea().setCaretPosition(getConsoleTextArea().getDocument().getLength());
  }

  public void err(String text) {
    System.err.println(text);
    instance.getConsoleTextArea().append("*** " + text + "\r\n");
    getConsoleTextArea().setCaretPosition(getConsoleTextArea().getDocument().getLength());
  }
}
