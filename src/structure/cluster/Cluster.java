package structure.cluster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import structure.DiskType;
import structure.File;
import structure.Node;
import system.ClusterMeta;
import system.FileRead;
import system.FileWrite;
import system.HealthCheckReport;
import system.Location;

public abstract class Cluster {
  public static final int NO_SPACE_SENTINEL = -1337;

  public HashMap<Long, ClusterMeta> map;
  protected int id;
  protected Location location;
  protected DiskType diskType;
  protected ArrayList<Node> nodes;
  protected HashMap<Node, Set<Long>> nodeFileMap;
  protected long spaceAvailability;
  private boolean isHealthy;

  public Cluster(int id, Location location, int nodeCount, long idStart, DiskType diskType) {
    this.id = id;
    this.location = location;
    this.diskType = diskType;

    spaceAvailability = -1;
    map = new HashMap<Long, ClusterMeta>();

    nodes = new ArrayList<Node>();
    for (int i = 0; i < nodeCount; i++) {
      nodes.add(new Node(idStart + i, diskType));
    }

    nodeFileMap = new HashMap<Node, Set<Long>>();
    for (Node node : nodes) {
      nodeFileMap.put(node, new HashSet<Long>());
    }

    isHealthy = true;
  }

  /**
   * Reads a file with the given id from the given node.
   */
  public FileRead read(long fileId) {
    ClusterMeta cm = map.get(fileId);
    if (cm == null) {
      return null;
    }
    Node node = null;
    for (Node n : cm.instances) {
      if (n.isHealthy()) {
        node = n;
        break;
      }
    }
    if (node == null) {
      return new FileRead(null, -1);
    }
    FileRead fr = node.read(fileId);
    if (fr == null) {
      return new FileRead(null, -1);
    }
    fr.time += clusterDelay(node);
    return fr;
  }

  /**
   * After a cluster write or update fails, this method is called to tell the cluster to figure out
   * how much space it has left so that it can quickly accept or reject future writes or updates. 
   */
  public void calibrateSpaceAvailability(int redundancy) {
    long[] freeestSpaces = new long[redundancy];
    for (Node node : nodes) {
      if (!node.isHealthy()) {
        continue;
      }
      long freeSpace = node.freeSpace();
      for (int j = 0; j < redundancy; j++) {
        if (freeSpace > freeestSpaces[j]) {
          for (int k = redundancy - 2; k >= j; k--) {
            freeestSpaces[k + 1] = freeestSpaces[k];
          }
          freeestSpaces[j] = freeSpace;
          break;
        }
      }
    }
    spaceAvailability = freeestSpaces[redundancy - 1];
  }

  public double avgUsage() {
    double sumUsage = 0;
    for (Node node : nodes) {
      sumUsage += node.getUsage();
    }
    return sumUsage / nodes.size();
  }

  public HealthCheckReport healthCheck() {
    HashMap<Long, Integer> filesForNodeRewrite = new HashMap<Long, Integer>();
    int nodesDown = 0;
    for (Node node : nodes) {
      if (!node.isHealthy()) {
        nodesDown++;
        for (Long fileId : nodeFileMap.get(node)) {
          map.get(fileId).instances.remove(node);
          if (!filesForNodeRewrite.containsKey(fileId)) {
            filesForNodeRewrite.put(fileId, 0);
          }
          filesForNodeRewrite.put(fileId, filesForNodeRewrite.get(fileId) + 1);
        }
      }
    }
    HealthCheckReport report = new HealthCheckReport(nodesDown, location, new HashSet<Long>());
    for (Entry<Long, Integer> entry : filesForNodeRewrite.entrySet()) {
      File file = map.get(entry.getKey()).file;
      long nodeRewriteTime = write(file, entry.getValue()).time;
      if (nodeRewriteTime == -1) {
        // Then the file is not longer in the correctly redundant number of nodes. Wipe from all
        // nodes.
        for (Node node : map.get(entry.getKey()).instances) {
          node.delete(file);
        }
        report.fileIdsForRewrite.add(entry.getKey());
      }
    }
    return report;
  }

  protected abstract long clusterDelay(Node[] nodes);

  protected long clusterDelay(Node node) {
    return clusterDelay(new Node[] {node});
  }

  public abstract FileWrite write(File f, int redundancy);

  public abstract void printState(String indentation, boolean nodeDetails, boolean fileDetails);

  public Location getLocation() {
    return location;
  }

  public int getId() {
    return id;
  }

  public DiskType getDiskType() {
    return diskType;
  }

  public int getNodeCount() {
    return nodes.size();
  }

  public boolean isHealthy() {
    return isHealthy;
  }

  public void setHealthy(boolean healthy) {
    isHealthy = healthy;
  }

  public void fail() {
    setHealthy(false);
  }
}
