package ui;

public class ClusterUIModel {
  public int id;
  public double avgUsage;
  public int nodesDown;
  public String titleString;
  public String locationString;

  public ClusterUIModel(int id, double avgUsage, String titleString, String locationString) {
    this.id = id;
    this.avgUsage = avgUsage;
    this.titleString = titleString;
    this.locationString = locationString;
    nodesDown = 0;
  }
}
