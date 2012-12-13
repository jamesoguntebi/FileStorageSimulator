package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;

public class MainPanel extends JPanel {
  private static final long serialVersionUID = 1L;

  private static MainPanel instance;

  public static MainPanel get() {
    if (instance == null) {
      instance = new MainPanel();
    }
    return instance;
  }

  private MainPanel() {
    setLayout(new BorderLayout());

    add(getCenterPanel(), BorderLayout.CENTER);
    add(getWestPanel(), BorderLayout.WEST);
  }

  private JPanel centerPanel;

  private JPanel getCenterPanel() {
    if (centerPanel == null) {
      centerPanel = new JPanel();
      centerPanel.setLayout(new BorderLayout());

      centerPanel.add(ClusterPanel.get(), BorderLayout.CENTER);
      centerPanel.add(ConsolePanel.get(), BorderLayout.SOUTH);
    }
    return centerPanel;
  }

  private JPanel westPanel;

  private JPanel getWestPanel() {
    if (westPanel == null) {
      westPanel = new JPanel();
      westPanel.setLayout(new BorderLayout());
      westPanel.setPreferredSize(new Dimension(350, 700));
      westPanel.setBackground(Color.BLACK);

      westPanel.add(ControlPanel.get(), BorderLayout.NORTH);
      westPanel.add(StatisticsPanel.get(), BorderLayout.CENTER);
    }
    return westPanel;
  }
}
