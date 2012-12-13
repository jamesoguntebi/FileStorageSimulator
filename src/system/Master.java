package system;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.SwingWorker;

import logic.Algorithm;
import requestor.ReadRequest;
import requestor.Request;
import requestor.RequestStreamModel;
import requestor.UpdateRequest;
import requestor.WriteRequest;
import requestor.user_based.UserBasedStream;
import structure.DiskType;
import structure.cluster.Cluster;
import structure.cluster.SqrGrid;
import ui.ClusterList;
import ui.ConsolePanel;
import ui.StatisticsPanel;

public class Master extends SwingWorker<Void, Void> {
  private static final int UI_UPDATE_FREQ = 500; // in milliseconds

  public HashMap<Long, MasterMeta> map;
  public ArrayList<Cluster> clusters;

  // Analytics values.
  int requestCount;
  int writeCount = 0;
  int writeFailCount = 0;
  long sumWriteDelay = 0;
  int updateCount = 0;
  int updateFailCount = 0;
  long sumUpdateDelay = 0;
  int readCount = 0;
  int readFailCount = 0;
  long sumReadDelay = 0;
  long totalDuration = 0;

  private Algorithm algorithm;
  private RequestStreamModel requestStream;

  private boolean handlingRequests = true;
  private long lastStateDump = System.currentTimeMillis();

  private static Master instance;

  public static Master getStaticInstance() {
    if (instance == null) {
      ConsolePanel.get().err("The simulator Master object must first be instantiated.");
    }
    return instance;
  }

  public static void create(int clusterCount, int nodesPerCluster, DiskType diskType,
      Algorithm algo, RequestStreamModel rsm) {
    instance = new Master();
    instance.map = new HashMap<Long, MasterMeta>();
    instance.clusters = new ArrayList<Cluster>();

    instance.algorithm = algo;
    instance.requestStream = rsm;

    instance.placeClusters(clusterCount, nodesPerCluster, diskType);
    algo.init(instance);
  }

  private void placeClusters(int clusterCount, int nodeCount, DiskType dt) {
    ArrayList<Location> clusterLocations = new ArrayList<Location>();
    for (int i = 0; i < clusterCount; i++) {
      Location l;
      boolean badSpot;
      do {
        l = Util.getRandomLocation();
        badSpot = false;
        for (Cluster c : clusters) {
          if (c.getLocation().distance(l) < FssConstants.MIN_CLUSTER_DISTANCE) {
            badSpot = true;
            break;
          }
        }
      } while (badSpot);
      clusterLocations.add(l);
      SqrGrid g = new SqrGrid(i, l, nodeCount, 1 + i * nodeCount, dt);
      clusters.add(g);
    }
    if (requestStream instanceof UserBasedStream) {
      ((UserBasedStream) requestStream).instantiateUsers(clusterLocations);
    }
  }

  @Override
  protected Void doInBackground() throws Exception {
    while (true) {
      if (handlingRequests) {
        handleRequest(requestStream.getNextRequest());
        if (System.currentTimeMillis() - lastStateDump > UI_UPDATE_FREQ) {
          ClusterList.get().updateClusterData(clusters);
          StatisticsPanel.get().updateSimData(requestCount, Util.avgNetworkTime(),
              writeCount, writeFailCount, (double) sumWriteDelay / (writeCount - writeFailCount),
              updateCount, updateFailCount,
                  (double) sumUpdateDelay / (updateCount - updateFailCount),
              readCount, readFailCount, (double) sumReadDelay / readCount);
          firePropertyChange("SIM_STATS_UPDATED", 0, 1);
          lastStateDump = System.currentTimeMillis();
        }
      }
    }
  }

  public void pause() {
    handlingRequests = false;
  }

  public void resume() {
    handlingRequests = true;
  }

  public void flushSimState() {
    requestStream.flushFileHistory();
    if (requestStream instanceof UserBasedStream) {
      ((UserBasedStream) requestStream).flushUserList();
    }
    Util.flushStatistics();
  }

  public WriteResult initializeWithWrites(long count, RequestStreamModel requestModel) {
    long sumWriteTime = 0;
    long fails = 0;
    for (long i = 0; i < count; i++) {
      long writeTime = handleRequest(requestModel.getNextInitializtionRequest());
      if (writeTime > 0) {
        sumWriteTime += writeTime;
      } else {
        fails++;
      }
    }
    return new WriteResult(count, sumWriteTime / (count - fails), (double) fails / count);
  }

  public long handleRequest(Request req) {
    requestCount++;
    long delay = -1;
    switch (req.getRequestType()) {
    case WRITE:
      writeCount++;
      delay = algorithm.doWrite(this, (WriteRequest) req);
      if (delay > 0) {
        sumWriteDelay += delay;
      } else {
        writeFailCount++;
      }
      break;
    case UPDATE:
      updateCount++;
      delay = algorithm.doUpdate(this, (UpdateRequest) req);
      if (delay > 0) {
        sumUpdateDelay += delay;
      } else {
        updateFailCount++;
      }
      break;
    case READ:
      readCount++;
      delay = algorithm.doRead(this, (ReadRequest) req);
      if (delay > 0) {
        sumReadDelay += delay;
      } else {
        readFailCount++;
      }
      break;
    default:
      System.err.print("Unknown request type: " + req.getRequestType());
      System.exit(0);
    }
    return delay;
  }

  public RequestStreamModel getRequestStreamModel() {
    return requestStream;
  }

  public void printState(boolean clusterDetails, boolean nodeDetails, boolean fileDetails) {
    ConsolePanel.get().out(clusters.size() + " clusters containing " + map.size() + " files.");
    if (clusterDetails) {
      for (Cluster c : clusters) {
        c.printState("    ", nodeDetails, fileDetails);
      }
    }
  }

  public void printStatistics() {
    ConsolePanel.get().out("\n" + (requestCount - 1) + " total actions");
    ConsolePanel.get().out("\nFresh writes:");
    ConsolePanel.get().out(this.new WriteResult(writeCount, sumWriteDelay
        / (writeCount - writeFailCount), (double) writeFailCount / writeCount).toString());
    ConsolePanel.get().out("\nWrite updates:");
    ConsolePanel.get().out(this.new WriteResult(updateCount, sumUpdateDelay
        / (updateCount - updateFailCount), (double) updateFailCount / updateCount).toString());
    ConsolePanel.get().out("\nReads:");
    ConsolePanel.get().out(this.new ReadResult(readCount, sumReadDelay / readCount).toString());

    ConsolePanel.get().out("\naverage network time: " + Util.avgNetworkTime());
  }

  public class WriteResult {
    public long count;
    public long avgWriteTime;
    public double percentFailure;

    public WriteResult(long count, long avgWriteTime, double percentFailure) {
      this.count = count;
      this.avgWriteTime = avgWriteTime;
      this.percentFailure = percentFailure;
    }

    public String toString() {
      String failureRate = String.format("%.2f", percentFailure * 100);
      return count + " writes performed, taking avg " + avgWriteTime + " ms. " + failureRate
          + "% failed.";
    }
  }

  public class ReadResult {
    public long count;
    public long avgReadTime;

    public ReadResult(long count, long avgWriteTime) {
      this.count = count;
      this.avgReadTime = avgWriteTime;
    }

    public String toString() {
      return count + " reads performed, taking avg " + avgReadTime + " ms.";
    }
  }

  /**
   * @param fatal If the file state is no longer guaranteed in the system. False if the only failure
   *              is that redundancy is no longer as desired.
   */
  public void handleWriteFailureDueToNodeFailure(boolean fatal) {
    ConsolePanel.get().out((fatal ? "Fatal" : "Soft") + " write failure due to node failure.");
  }
}
