package requestor;

import java.util.Random;

import structure.File;
import system.Util;

public class NetworkComputationStream extends RequestStreamModel {
  private static Random rand = new Random();

  private static NetworkComputationStream instance;

  public static NetworkComputationStream get() {
    if (instance == null) {
      instance = new NetworkComputationStream();
    }
    return instance;
  }

  private NetworkComputationStream() {
    super();
  };

  @Override
  public Request getNextRequest() {
    double accessType = rand.nextDouble();
    if (accessType < (double) 1 / 5) {
      return new WriteRequest(createFreshRandomFile());
    } else if (accessType < (double) 7 / 10) {
      long fileId = fileIds.get(rand.nextInt(fileIds.size()));
      File existingFile = new File(Util.getRandomFileSize(), fileId);
      return new UpdateRequest(existingFile);
    } else {
      long existingFileId = fileIds.get(rand.nextInt(fileIds.size()));
      return new ReadRequest(existingFileId);
    }
  }

  @Override
  public WriteRequest getNextInitializtionRequest() {
    return new WriteRequest(createFreshRandomFile());
  }

  @Override
  public void printState(boolean verbose) {
//    System.out.println("I am the random requestor model; I have nothing interesting to print :(.");
  }

  @Override
  public String toString() {
    return "Network Computation";
  }
}
