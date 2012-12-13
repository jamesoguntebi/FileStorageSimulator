package system;

import structure.Node;

public class FileWrite {
  public long time;
  public Node[] nodes;

  public FileWrite(Node[] nodes, long time) {
    this.nodes = nodes;
    this.time = time;
  }
}