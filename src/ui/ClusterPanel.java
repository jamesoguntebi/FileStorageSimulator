package ui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class ClusterPanel extends JPanel {
  private static final long serialVersionUID = 1L;

  private static ClusterPanel instance;

  public static ClusterPanel get() {
    if (instance == null) {
      instance = new ClusterPanel();
    }
    return instance;
  }

  private ClusterPanel() {
    setLayout(new BorderLayout());

    add(getClusterListScrollPane(), BorderLayout.CENTER);
  }

  private JScrollPane clusterListScrollPane;

  private JScrollPane getClusterListScrollPane() {
    if (clusterListScrollPane == null) {
      clusterListScrollPane = new JScrollPane(ClusterList.get());
    }
    return clusterListScrollPane;
  }
}
