package system;

import java.util.ArrayList;

import structure.File;
import structure.Node;

public class ClusterMeta {
  public File file;
  public ArrayList<Node> instances;

  public ClusterMeta(File file) {
    this.file = file;
    instances = new ArrayList<Node>();
  }
}
