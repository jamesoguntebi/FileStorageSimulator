package logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import requestor.ReadRequest;
import requestor.UpdateRequest;
import requestor.WriteRequest;
import structure.File;
import structure.cluster.Cluster;
import system.HealthCheckReport;
import system.Location;
import system.Master;
import ui.ClusterList;

public abstract class Algorithm {
  protected static final Random random = new Random();
  private static final int RECALIBRATION_FREQ = 50000;
  private static final int NODE_HEALTH_CHECK_FREQ = 50000;

  protected int accessCount;

  protected Algorithm() {
    accessCount++;
  }

  protected void incrementAccessAndMaybeRecalibrate(Master m) {
    accessCount++;
    if (accessCount % RECALIBRATION_FREQ == 0) {
      for (Cluster c : m.clusters) {
        c.calibrateSpaceAvailability(getNodePerClusterRedundancy());
      }
    }

    if (accessCount % NODE_HEALTH_CHECK_FREQ == 0) {
      HashMap<Long, List<Location>> filesForClusterRewrite = new HashMap<Long, List<Location>>();
      Map<Integer, Integer> nodesDownMap = new HashMap<Integer, Integer>();
      for (Cluster c : m.clusters) {
        HealthCheckReport report = c.healthCheck();
        if (!report.fileIdsForRewrite.isEmpty()) {
          c.calibrateSpaceAvailability(getNodePerClusterRedundancy());
        }
        for (long fileId : report.fileIdsForRewrite) {
          m.map.get(fileId).instances.remove(c);
          if (!filesForClusterRewrite.containsKey(fileId)) {
            filesForClusterRewrite.put(fileId, new ArrayList<Location>());
          }
          filesForClusterRewrite.get(fileId).add(report.location);
        }
        nodesDownMap.put(c.getId(), report.nodesDown);
      }

      ClusterList.get().updateClusterNodeHealth(nodesDownMap);
      boolean allRewritesSucceeded = true;
      boolean fatalFailure = false;
      for (Entry<Long, List<Location>> entry : filesForClusterRewrite.entrySet()) {
        File file = m.map.get(entry.getKey()).file;
        long rewriteTime =
            doWrite(m, new WriteRequest(file, entry.getValue().get(0)), entry.getValue().size());
        if (rewriteTime == -1) {
          allRewritesSucceeded = false;
          if (m.map.get(entry.getKey()).instances.isEmpty()) {
            fatalFailure = true;
          }
        }
      }
      if (!allRewritesSucceeded) {
        m.handleWriteFailureDueToNodeFailure(fatalFailure);
      }
    }
  }

  public abstract long doWrite(Master master, WriteRequest writeRequest, int redundancy);
  public abstract long doWrite(Master master, WriteRequest writeRequest);
  public abstract long doUpdate(Master master, UpdateRequest writeRequest);
  public abstract long doRead(Master master, ReadRequest writeRequest);
  public abstract void init(Master master);
  protected abstract int getNodePerClusterRedundancy();

  public abstract String toString();

  public static Algorithm[] getAlgorithms() {
    return new Algorithm[] {
      DefaultAlgo.get(),
      BatalFiatRabani.get(),
      UMichNetworkOpt.get(),
      Chu01LinProgram.get()
    };
  }
}
