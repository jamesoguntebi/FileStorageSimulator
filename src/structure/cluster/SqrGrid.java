package structure.cluster;

import structure.DiskType;
import structure.File;
import structure.Node;
import system.ClusterMeta;
import system.FileWrite;
import system.Location;
import ui.ConsolePanel;

public class SqrGrid extends Cluster {
  private Node[][] gridNodes;
  private int gridLen;
  private int nextNode;

  public SqrGrid(int id, Location location, int nodeCount, long idStart, DiskType diskType) {
    super(id, location, nodeCount, idStart, diskType);

    nextNode = 0;
    gridLen = (int) Math.sqrt(nodeCount);
    gridNodes = new Node[gridLen][gridLen];
    for (int i = 0; i < gridLen; i++) {
      for (int j = 0; j < gridLen; j++) {
        gridNodes[i][j] = nodes.get(i * gridLen + j);
      }
    }
  }

  @Override
  public FileWrite write(File f, int redundancy) {
    if (spaceAvailability != -1 && spaceAvailability < f.getSize()) {
      return new FileWrite(new Node[0], NO_SPACE_SENTINEL);
    }

    ClusterMeta cm = null;
    if (map.containsKey(f.getId())) {
      cm = map.get(f.getId());
    } else {
      cm = new ClusterMeta(f);
      map.put(f.getId(), cm);
    }

    Node[] writtenNodes = new Node[redundancy];
    int i = 0;
    long maxWrite = -1;
    int startNode = nextNode;
    while (i < redundancy) {
      int xNode = nextNode % gridLen;
      int yNode = nextNode / gridLen;
      Node writeNode = gridNodes[xNode][yNode];
      if (!writeNode.isHealthy()) {
        nextNode = (nextNode + 1) % (gridLen * gridLen);
        if (nextNode == startNode) { // all nodes were tried and redundancy was never reached
          // erase the file from the nodes that DID do the write
          for (int j = 0; j < i; j++) {
            writtenNodes[j].delete(f);
          }
          return new FileWrite(writtenNodes, -1);
        }
        continue;
      }
      long writeTime = writeNode.store(f);
      if (writeTime > 0) { // Node had sufficient space.
        maxWrite = Math.max(maxWrite, writeTime);
        writtenNodes[i] = writeNode;
        cm.instances.add(writeNode);
        nodeFileMap.get(writeNode).add(f.getId());
        i++;
      }
      nextNode = (nextNode + 1) % (gridLen * gridLen);
      if (nextNode == startNode) { // all nodes were tried and redundancy was never reached
        // erase the file from the nodes that DID do the write
        for (int j = 0; j < i; j++) {
          writtenNodes[j].delete(f);
        }
        return new FileWrite(writtenNodes, -1);
      }
    }
    return new FileWrite(writtenNodes, maxWrite > 0 ? clusterDelay(writtenNodes) + maxWrite : -1);
  }

  @Override
  protected long clusterDelay(Node[] nodes) {
    int maxGridDist = -1;
    for (Node node : nodes) {
      for (int i = 0; i < gridLen; i++) {
        for (int j = 0; j < gridLen; j++) {
          if (gridNodes[i][j] == node) {
            maxGridDist = Math.max(maxGridDist, i + j);
            i = gridLen;
            j = gridLen;
          }
        }
      }
    }
    return 15 * maxGridDist;
  }

  public void printState(String indentation, boolean nodeDetails, boolean fileDetails) {
    String avgUsage = String.format("%.2f", avgUsage() * 100);
    ConsolePanel.get().out(indentation + (gridLen * gridLen) + " nodes containing " + map.size()
        + " files. Avg usage: " + avgUsage + "%");

    if (nodeDetails) {
      for (int i = 0; i < gridLen; i++) {
        for (int j = 0; j < gridLen; j++) {
          gridNodes[i][j].printState(indentation + "    ", fileDetails);
        }
      }
    }
  }
}
