package requestor;

import java.util.Random;

import structure.File;
import system.Util;

public class RandomRequestor extends RequestStreamModel {
  private static Random rand = new Random();

  private static RandomRequestor instance;

  public static RandomRequestor get() {
    if (instance == null) {
      instance = new RandomRequestor();
    }
    return instance;
  }

  private RandomRequestor() {
    super();
  };

  @Override
  public Request getNextRequest() {
    double accessType = rand.nextDouble();
    if (accessType < (double) 1 / 3) {
      return new WriteRequest(createFreshRandomFile());
    } else if (accessType < (double) 2 / 3) {
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
    return "Random";
  }
}
