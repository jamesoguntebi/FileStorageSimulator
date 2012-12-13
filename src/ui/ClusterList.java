package ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.AbstractListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.MatteBorder;

import structure.DiskType;
import structure.cluster.Cluster;

public class ClusterList extends JList<ClusterUIModel> implements PropertyChangeListener {
  private static final long serialVersionUID = 1L;

  private ArrayList<ClusterUIModel> clusters;

  private static ClusterList instance;

  public static ClusterList get() {
    if (instance == null) {
      instance = new ClusterList(new ClusterListModel());
    }
    return instance;
  }

  private ClusterList(AbstractListModel<ClusterUIModel> model) {
    super(model);
    setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED),
        new MatteBorder(4, 4, 4, 4, MainFrame.DARK_BLUE)));
    setCellRenderer(new ClusterRenderer());
    clusters = new ArrayList<ClusterUIModel>();
  }

  public void updateClusterData(ArrayList<Cluster> backendClusters) {
    if (clusters.size() == 0 || clusters.get(0).id == -1) {
      clusters.clear();
      for (Cluster c : backendClusters) {
        String titleString = c.getDiskType() == DiskType.SSD ? "Solid State" : "Hard Disk";
        String locationString = String.format("lat: %.3f, long: %.3f", c.getLocation().getLat(), c
            .getLocation().getLng());
        titleString += " cluster with " + c.getNodeCount() + " nodes";
        clusters.add(new ClusterUIModel(c.getId(), c.avgUsage(), titleString, locationString));
      }
    } else {
      for (Cluster c : backendClusters) {
        for (ClusterUIModel uic : clusters) {
          if (uic.id == c.getId()) {
            uic.avgUsage = c.avgUsage();
            break;
          }
        }
      }
    }
  }

  public void updateClusterNodeHealth(Map<Integer, Integer> nodesDownMap) {
    for (ClusterUIModel uic : clusters) {
      if (nodesDownMap.containsKey(uic.id)) {
        uic.nodesDown = nodesDownMap.get(uic.id);
      }
    }
  }

  public void clearClusterData() {
    clusters.clear();
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    if (evt.getPropertyName().equals("SIM_STATS_UPDATED")) {
      updateUI();
    }
  }

  /**
   * An object subclassing {@link JPanel} that paints state for a Cluster.
   * 
   * @author James Oguntbei
   */
  private static class ClusterRenderer extends JPanel implements ListCellRenderer<ClusterUIModel> {
    private static final long serialVersionUID = 1L;

    final static int H_MARGIN = 30;
    final static int V_MARGIN = 10;
    final static int PROGRESS_BAR_HEIGHT = 30;
    final static Color PROGRESS_BAR_BG = MainFrame.DARK_BLUE;
    final static Color PROGRESS_BAR_FG = new Color(30, 80, 255);
    final static Color PROGRESS_BAR_BORDER_COLOR = MainFrame.DARK_BLUE;
    final static Color NODE_HEALTH_GOOD_COLOR = new Color(0, 150, 0);
    final static Color NODE_HEALTH_BAD_COLOR = Color.RED;
    final static Color LIST_BG_1 = Color.WHITE;
    final static Color LIST_BG_2 = new Color(192, 192, 192);
    final static Font TITLE_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 14);
    final static Font LOCATION_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 13);

    private ClusterUIModel cluster;

    public Component getListCellRendererComponent(JList<? extends ClusterUIModel> list,
        ClusterUIModel value, int index, boolean isSelected, boolean cellHasFocus) {
      cluster = value;

      setLayout(null);
      setPreferredSize(new Dimension(getWidth(), 80));
      setBorder(new MatteBorder(3, 3, 3, 3, MainFrame.DARK_BLUE));
      setBackground(index % 2 == 0 ? LIST_BG_1 : LIST_BG_2);

      return this;
    }

    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2 = (Graphics2D) g;
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

      g2.setColor(MainFrame.DARK_BLUE);
      g2.setFont(TITLE_FONT);
      g2.drawString(cluster.titleString, H_MARGIN, V_MARGIN + TITLE_FONT.getSize());

      g2.setFont(LOCATION_FONT);
      FontMetrics fm = g.getFontMetrics();
      int locationWidth = fm.stringWidth(cluster.locationString);
      g2.drawString(cluster.locationString, getWidth() - H_MARGIN - locationWidth, V_MARGIN
          + LOCATION_FONT.getSize());

      int width = getWidth() - H_MARGIN - H_MARGIN;

      g.setColor(PROGRESS_BAR_BG);
      g.fillRect(H_MARGIN, getHeight() - V_MARGIN - PROGRESS_BAR_HEIGHT, width, PROGRESS_BAR_HEIGHT);
      g.setColor(PROGRESS_BAR_FG);
      g.fillRect(H_MARGIN, getHeight() - V_MARGIN - PROGRESS_BAR_HEIGHT,
          (int) (cluster.avgUsage * width), PROGRESS_BAR_HEIGHT);

      g2.setStroke(new BasicStroke(2));
      g2.setColor(PROGRESS_BAR_BORDER_COLOR);
      g2.drawRect(H_MARGIN, getHeight() - V_MARGIN - PROGRESS_BAR_HEIGHT, width,
          PROGRESS_BAR_HEIGHT);

      g2.setColor(Color.WHITE);
      g2.setFont(TITLE_FONT);
      g2.drawString(String.format("%.2f%% capacity", cluster.avgUsage * 100), H_MARGIN + 20,
          getHeight() - V_MARGIN - 10);

      g2.setColor(cluster.nodesDown == 0 ? NODE_HEALTH_GOOD_COLOR : NODE_HEALTH_BAD_COLOR);
      fm = g.getFontMetrics();
      String nodeHealthString = cluster.nodesDown + " nodes down";
      int nodeHealthWidth = fm.stringWidth(nodeHealthString);
      g2.drawString(nodeHealthString, getWidth() - H_MARGIN * 2 - nodeHealthWidth,
          getHeight() - V_MARGIN - 10);
    }
  }

  /**
   * @see AbstractListModel
   * @author James Oguntebi
   */
  private static class ClusterListModel extends AbstractListModel<ClusterUIModel> {
    private static final long serialVersionUID = 1L;

    /**
     * @see AbstractListModel#getElementAt(int)
     */
    @Override
    public ClusterUIModel getElementAt(int i) {
      return ClusterList.get().clusters.get(i);
    }

    /**
     * @see AbstractListModel#getSize()
     */
    @Override
    public int getSize() {
      return ClusterList.get().clusters.size();
    }
  }
}
