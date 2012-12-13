package system;

import java.util.Set;

public class HealthCheckReport {
  public int nodesDown;
  public Location location;
  public Set<Long> fileIdsForRewrite;

  public HealthCheckReport(int nodesDown, Location location, Set<Long> fileIdsForRewrite) {
    this.nodesDown = nodesDown;
    this.location = location;
    this.fileIdsForRewrite = fileIdsForRewrite;
  }
}
