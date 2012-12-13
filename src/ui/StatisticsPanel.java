package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.MatteBorder;

public class StatisticsPanel extends JPanel implements PropertyChangeListener {
  private static final long serialVersionUID = 1L;

  private static StatisticsPanel instance;

  public static StatisticsPanel get() {
    if (instance == null) {
      instance = new StatisticsPanel();
    }
    return instance;
  }

  private StatisticsPanel() {
    setBackground(MainFrame.DARK_BLUE);
    setLayout(new GridBagLayout());
    setBorder(new CompoundBorder(new BevelBorder(BevelBorder.RAISED),
        new MatteBorder(4, 4, 4, 4, Color.GRAY)));
    GridBagConstraints gbc = new GridBagConstraints();

    gbc.gridx = 1;
    gbc.gridy = 0;
    add(Box.createHorizontalStrut(15), gbc);

    gbc.gridx = 2;
    add(getTotalActionsLabel(), gbc);

    gbc.gridy = 1;
    add(getAvgNetworkTimeLabel(), gbc);

    gbc.gridy = 2;
    add(Box.createVerticalStrut(20), gbc);

    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.gridheight = 3;
    add(getWritesChartPanel(), gbc);

    gbc.gridx = 2;
    gbc.gridheight = 1;
    add(getTotalWritesLabel(), gbc);

    gbc.gridy = 4;
    add(getFailedWritesLabel(), gbc);

    gbc.gridy = 5;
    add(getAvgWriteTimeLabel(), gbc);

    gbc.gridy = 6;
    add(Box.createVerticalStrut(20), gbc);

    gbc.gridx = 0;
    gbc.gridy = 7;
    gbc.gridheight = 3;
    add(getUpdatesChartPanel(), gbc);

    gbc.gridx = 2;
    gbc.gridheight = 1;
    add(getTotalUpdatesLabel(), gbc);

    gbc.gridy = 8;
    add(getFailedUpdatesLabel(), gbc);

    gbc.gridy = 9;
    add(getAvgUpdateTimeLabel(), gbc);

    gbc.gridy = 10;
    add(Box.createVerticalStrut(20), gbc);

    gbc.gridx = 0;
    gbc.gridy = 11;
    gbc.gridheight = 3;
    add(getReadsChartPanel(), gbc);

    gbc.gridx = 2;
    gbc.gridheight = 1;
    add(getTotalReadsLabel(), gbc);

    gbc.gridy = 12;
    add(getFailedReadsLabel(), gbc);

    gbc.gridy = 13;
    add(getAvgReadTimeLabel(), gbc);
  }

  private JLabel totalActionsLabel;

  private JLabel getTotalActionsLabel() {
    if (totalActionsLabel == null) {
      totalActionsLabel = getDefaultStatLabel();
      totalActionsLabel.setText("0 Total actions");
    }
    return totalActionsLabel;
  }

  public void updateTotalActions(int totalActions) {
    getTotalActionsLabel().setText(totalActions + " Total actions");
  }

  private JLabel avgNetworkTimeLabel;

  private JLabel getAvgNetworkTimeLabel() {
    if (avgNetworkTimeLabel == null) {
      avgNetworkTimeLabel = getDefaultStatLabel();
      avgNetworkTimeLabel.setText("--- ms avg network time");
    }
    return avgNetworkTimeLabel;
  }

  public void updateAvgNetworkTime(long avgNetworkTime) {
    getAvgNetworkTimeLabel().setText(String.format("%d ms avg network time", avgNetworkTime));
  }

  private FssPieChartPanel writesChartPanel;

  private FssPieChartPanel getWritesChartPanel() {
    if (writesChartPanel == null) {
      writesChartPanel = getDefaultPieChart();
    }
    return writesChartPanel;
  }

  private JLabel totalWritesLabel;

  private JLabel getTotalWritesLabel() {
    if (totalWritesLabel == null) {
      totalWritesLabel = getDefaultStatLabel();
      totalWritesLabel.setText("0 Total writes");
    }
    return totalWritesLabel;
  }

  public void updateTotalWrites(int totalWrites) {
    getTotalWritesLabel().setText(totalWrites + " Total writes");
  }

  private JLabel failedWritesLabel;

  private JLabel getFailedWritesLabel() {
    if (failedWritesLabel == null) {
      failedWritesLabel = getDefaultStatLabel();
      failedWritesLabel.setText("0 writes failed (-- %)");
    }
    return failedWritesLabel;
  }

  private void updateWritesFailed(int failedWrites, double proportion) {
    getFailedWritesLabel().setText(String.format("%d Failed (%.2f %%)", failedWrites,
        proportion * 100));
    getWritesChartPanel().setGoodProportion(1 - proportion);
  }

  private JLabel avgWriteTimeLabel;

  private JLabel getAvgWriteTimeLabel() {
    if (avgWriteTimeLabel == null) {
      avgWriteTimeLabel = getDefaultStatLabel();
      avgWriteTimeLabel.setText("--- ms avg write time");
    }
    return avgWriteTimeLabel;
  }

  private void updateAvgWriteTime(double avgWriteTime) {
    getAvgWriteTimeLabel().setText(String.format("%.1f ms avg write time", avgWriteTime));
  }

  private FssPieChartPanel updatesChartPanel;

  private FssPieChartPanel getUpdatesChartPanel() {
    if (updatesChartPanel == null) {
      updatesChartPanel = getDefaultPieChart();
    }
    return updatesChartPanel;
  }

  private JLabel totalUpdatesLabel;

  private JLabel getTotalUpdatesLabel() {
    if (totalUpdatesLabel == null) {
      totalUpdatesLabel = getDefaultStatLabel();
      totalUpdatesLabel.setText("0 Total updates");
    }
    return totalUpdatesLabel;
  }

  private void updateTotalUpdates(int totalUpdates) {
    getTotalUpdatesLabel().setText(totalUpdates + " Total updates");
  }

  private JLabel failedUpdatesLabel;

  private JLabel getFailedUpdatesLabel() {
    if (failedUpdatesLabel == null) {
      failedUpdatesLabel = getDefaultStatLabel();
      failedUpdatesLabel.setText("0 updates failed (-- %)");
    }
    return failedUpdatesLabel;
  }

  private void updateUpdatesFailed(int failedUpdates, double proportion) {
    getFailedUpdatesLabel().setText(String.format("%d Failed (%.2f %%)", failedUpdates,
        proportion * 100));
    getUpdatesChartPanel().setGoodProportion(1 - proportion);
  }

  private JLabel avgUpdateTimeLabel;

  private JLabel getAvgUpdateTimeLabel() {
    if (avgUpdateTimeLabel == null) {
      avgUpdateTimeLabel = getDefaultStatLabel();
      avgUpdateTimeLabel.setText("--- ms avg update time");
    }
    return avgUpdateTimeLabel;
  }

  private void updateAvgUpdateTime(double avgUpdateTime) {
    getAvgUpdateTimeLabel().setText(String.format("%.1f ms avg update time", avgUpdateTime));
  }

  private FssPieChartPanel readsChartPanel;

  private FssPieChartPanel getReadsChartPanel() {
    if (readsChartPanel == null) {
      readsChartPanel = getDefaultPieChart();
    }
    return readsChartPanel;
  }

  private JLabel totalReadsLabel;

  private JLabel getTotalReadsLabel() {
    if (totalReadsLabel == null) {
      totalReadsLabel = getDefaultStatLabel();
      totalReadsLabel.setText("0 Total reads");
    }
    return totalReadsLabel;
  }

  private void updateTotalReads(int totalReads) {
    getTotalReadsLabel().setText(totalReads + " Total reads");
  }

  private JLabel failedReadsLabel;

  private JLabel getFailedReadsLabel() {
    if (failedReadsLabel == null) {
      failedReadsLabel = getDefaultStatLabel();
      failedReadsLabel.setText("0 reads failed (-- %)");
    }
    return failedReadsLabel;
  }

  private void updateReadsFailed(int failedReads, double proportion) {
    getFailedReadsLabel().setText(String.format("%d Failed (%.2f %%)", failedReads,
        proportion * 100));
    getReadsChartPanel().setGoodProportion(1 - proportion);
  }

  private JLabel avgReadTimeLabel;

  private JLabel getAvgReadTimeLabel() {
    if (avgReadTimeLabel == null) {
      avgReadTimeLabel = getDefaultStatLabel();
      avgReadTimeLabel.setText("--- ms avg read time");
    }
    return avgReadTimeLabel;
  }

  private void updateAvgReadTime(double avgReadTime) {
    getAvgReadTimeLabel().setText(String.format("%.1f ms avg read time", avgReadTime));
  }



  public void updateSimData(int requestCount, long avgNetworkTime,
      int writeCount, int writeFailCount, double avgWriteTime,
      int updateCount, int updateFailCount, double avgUpdateTime,
      int readCount, int readFailCount, double avgReadTime) {
    updateTotalActions(requestCount);
    updateAvgNetworkTime(avgNetworkTime);
    updateTotalWrites(writeCount);
    updateWritesFailed(writeFailCount, (double) writeFailCount / writeCount);
    updateAvgWriteTime(avgWriteTime);
    updateTotalUpdates(updateCount);
    updateUpdatesFailed(updateFailCount, (double) updateFailCount / updateCount);
    updateAvgUpdateTime(avgUpdateTime);
    updateTotalReads(readCount);
    updateReadsFailed(readFailCount, (double) readFailCount / readCount);
    updateAvgReadTime(avgReadTime);
  }

  private JLabel getDefaultStatLabel() {
    JLabel label = new JLabel();
    label.setPreferredSize(new Dimension(160, 23));
    label.setFont(MainFrame.DEFAULT_FONT);
    label.setForeground(Color.WHITE);
    return label;
  }

  private FssPieChartPanel getDefaultPieChart() {
    FssPieChartPanel panel = new FssPieChartPanel();
    panel.setPreferredSize(new Dimension(70, 70));
    return panel;
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    if (evt.getPropertyName().equals("SIM_STATS_UPDATED")) {
      getWritesChartPanel().repaint();
      getUpdatesChartPanel().repaint();
    }
  }
}
