package logic;

import java.util.HashSet;
import java.util.Set;

import requestor.ReadRequest;
import requestor.UpdateRequest;
import requestor.WriteRequest;
import structure.File;
import structure.Node;
import structure.cluster.Cluster;
import system.ClusterMeta;
import system.FileRead;
import system.FileWrite;
import system.Location;
import system.Master;
import system.MasterMeta;
import system.Util;

public class DefaultAlgo extends Algorithm {
  private static final int NODE_PER_CLUSTER_WRITE_REDUNDANCY = 2;
  private static final int DEFAULT_CLUSTER_WRITE_REDUNDANCY = 2;

  private static DefaultAlgo instance;

  private int clusterWriteRedundancy;

  public static DefaultAlgo get() {
    if (instance == null) {
      instance = new DefaultAlgo();
    }
    return instance;
  }

  private DefaultAlgo() {
    super();

    clusterWriteRedundancy = DEFAULT_CLUSTER_WRITE_REDUNDANCY;
  };

  public void init(Master m) {
    clusterWriteRedundancy = Math.min(DEFAULT_CLUSTER_WRITE_REDUNDANCY, m.clusters.size());
  }

  /**
   * Write the data to {@link #NODE_PER_CLUSTER_WRITE_REDUNDANCY} nodes in
   * {@link #clusterWriteRedundacy} random clusters.
   */
  @Override
  public long doWrite(Master master, WriteRequest writeRequest) {
    return doWrite(master, writeRequest, clusterWriteRedundancy);
  }

  public long doWrite(Master master, WriteRequest writeRequest, int redundancy) {
    incrementAccessAndMaybeRecalibrate(master);
    MasterMeta fm = null;
    File f = writeRequest.getFile();
    if (master.map.containsKey(f.getId())) {
      fm = master.map.get(f.getId());
    } else {
      fm = new MasterMeta(f);
      master.map.put(f.getId(), fm);
    }

    Set<Cluster> attemptedClusters = new HashSet<Cluster>();
    for (Cluster c : master.clusters){
      if (!c.isHealthy()) {
        attemptedClusters.add(c);
      }
    }
    int writes = 0;
    long maxDelay = -1;
    Cluster targetCluster;
    int targetWriteCount = Math.min(master.clusters.size(), redundancy);
    while (writes < targetWriteCount && attemptedClusters.size() < master.clusters.size()) {
      targetCluster = null;
      do {
        targetCluster = master.clusters.get(random.nextInt(master.clusters.size()));
      } while (attemptedClusters.contains(targetCluster));
      FileWrite fw = targetCluster.write(f, NODE_PER_CLUSTER_WRITE_REDUNDANCY);
      if (fw.time > 0) { // Cluster had sufficient space.
        maxDelay = Math.max(maxDelay, fw.time +
            Util.networkTime(f.getSize(),
                targetCluster.getLocation().distance(writeRequest.getLocation())));
        fm.instances.add(targetCluster);
        writes++;
      } else if (fw.time != Cluster.NO_SPACE_SENTINEL) {
        targetCluster.calibrateSpaceAvailability(NODE_PER_CLUSTER_WRITE_REDUNDANCY);
      }
      attemptedClusters.add(targetCluster);
    }
    return writes == 0 ? -1 : maxDelay;
  }

  /**
   * For every node in the system containing this file, update it.
   */
  public long doUpdate(Master master, UpdateRequest updateRequest) {
    incrementAccessAndMaybeRecalibrate(master);
    File f = updateRequest.getFile();
    Location accessLoc = updateRequest.getLocation();
    MasterMeta fm = master.map.get(f.getId());
    boolean allSucceeded = true;
    long maxDelay = -1;
    Set<Cluster> clustersToRemove = new HashSet<Cluster>();
    for (Cluster c : fm.instances) {
      boolean allNodesSucceeded = true;
      ClusterMeta cm = c.map.get(f.getId());
      if (!c.isHealthy()) {
        allNodesSucceeded = false;
      } else {
        double cDist = c.getLocation().distance(accessLoc);
        long networkTime = Util.networkTime(f.getSize(), cDist);
        for (Node n : cm.instances) {
          long writeTime = n.store(f);
          if (writeTime > 0) {
            maxDelay = Math.max(maxDelay, writeTime + networkTime);
          } else {
            allSucceeded = false;
            allNodesSucceeded = false;
            break;
          }
        }
      }
      if (!allNodesSucceeded) {
        for (Node n : cm.instances) {
          n.delete(f);
        }
        cm.instances.clear();
        clustersToRemove.add(c);
      }
    }
    fm.instances.removeAll(clustersToRemove);
    long rewriteTime = updateRewrite(master, f, accessLoc, clustersToRemove.size());
    return allSucceeded ? maxDelay : (rewriteTime > 0 ? maxDelay + rewriteTime : -1);
  }

  /**
   * Writes.
   */
  private long updateRewrite(Master master, File f, Location accessLoc, int rewriteCount) {
    MasterMeta fm = null;
    if (master.map.containsKey(f.getId())) {
      fm = master.map.get(f.getId());
    } else {
      fm = new MasterMeta(f);
      master.map.put(f.getId(), fm);
    }

    int cCount = master.clusters.size();
    int writes = 0;
    long maxDelay = -1;
    for (int i = 0; i < cCount && writes < rewriteCount; i++) {
      Cluster cluster = master.clusters.get(i);
      if (fm.instances.contains(cluster) || !cluster.isHealthy()) {
        continue;
      }
      FileWrite fw = cluster.write(f, NODE_PER_CLUSTER_WRITE_REDUNDANCY);
      double distance = accessLoc.distance(cluster.getLocation());
      if (fw.time > 0) { // Cluster had sufficient space.
        maxDelay = Math.max(maxDelay, fw.time + Util.networkTime(f.getSize(), distance));
        fm.instances.add(cluster);
        writes++;
      } else if (fw.time != Cluster.NO_SPACE_SENTINEL) {
        cluster.calibrateSpaceAvailability(NODE_PER_CLUSTER_WRITE_REDUNDANCY);
      }
    }
    if (writes < rewriteCount) {
      return -1;
    }
    return maxDelay;
  }

  /**
   * Read the data from a random cluster in file metadata table.
   */
  @Override
  public long doRead(Master master, ReadRequest readRequest) {
    incrementAccessAndMaybeRecalibrate(master);
    long fileId = readRequest.getFileId();
    MasterMeta fm = master.map.get(fileId);
    if (fm == null || fm.instances.size() == 0) {
      return -1;
    }
    long delay = -1;
    Set<Cluster> attemptedClusters = new HashSet<Cluster>();
    while (delay == -1 && attemptedClusters.size() < fm.instances.size()) {
      Cluster targetCluster = null;
      for (Cluster c : fm.instances) {
        if (!c.isHealthy()) {
          attemptedClusters.add(c);
          continue;
        }
        targetCluster = c;
        break;
      }
      FileRead fr = targetCluster.read(fileId);
      if (fr.time > 0) {
        delay = fr.time + Util.networkTime(fr.file.getSize(), 
            Util.networkTime(fr.file.getSize(),
                targetCluster.getLocation().distance(readRequest.getLocation())));
      }
      attemptedClusters.add(targetCluster);
    }
    return delay;
  }

  @Override
  protected int getNodePerClusterRedundancy() {
    return NODE_PER_CLUSTER_WRITE_REDUNDANCY;
  }

  @Override
  public String toString() {
    return "Default Algorithm";
  }
}
