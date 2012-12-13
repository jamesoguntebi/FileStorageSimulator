package system;

import java.util.ArrayList;

import structure.File;
import structure.cluster.Cluster;

public class MasterMeta {
  public File file;
  public ArrayList<Cluster> instances;

  public MasterMeta(File file) {
    this.file = file;
    instances = new ArrayList<Cluster>();
  }
}
