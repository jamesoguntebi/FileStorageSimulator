package ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

public class FssPieChartPanel extends JPanel {
  private static final long serialVersionUID = 1L;

  private static final int MARGIN = 5;

  private Color goodColor;
  private Color badColor;
  private double goodProportion = 1.0;

  public FssPieChartPanel(Color goodColor, Color badColor) {
    this.goodColor = goodColor;
    this.badColor = badColor;
    setBackground(null);
  }

  public FssPieChartPanel() {
    this(Color.GREEN, Color.RED);
  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    g.setColor(badColor);
    g.fillOval(MARGIN, MARGIN, getWidth() - 2 * MARGIN, getHeight() - 2 * MARGIN);
    g.setColor(goodColor);
    g.fillArc(MARGIN, MARGIN, getWidth() - 2 * MARGIN, getHeight() - 2 * MARGIN, 90,
        - (int) (goodProportion * 360));

    Graphics2D g2 = (Graphics2D) g;
    g2.setStroke(new BasicStroke(4));
    g2.setColor(Color.WHITE);
    g2.drawOval(MARGIN, MARGIN, getWidth() - 2 * MARGIN, getHeight() - 2 * MARGIN);
  }

  public void setGoodProportion(double goodProportion) {
    this.goodProportion = goodProportion;
    repaint();
  }
}
