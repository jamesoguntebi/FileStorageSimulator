package requestor.user_based;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import requestor.ReadRequest;
import requestor.Request;
import requestor.RequestStreamModel;
import requestor.UpdateRequest;
import requestor.WriteRequest;
import structure.File;
import system.Location;
import system.Util;
import ui.ConsolePanel;

public class UserBasedStream extends RequestStreamModel {
  private static Random rand = new Random();
  private static final int INITIAL_USER_COUNT = 5;

  // This is divided by the number of users to get the actual frequency.
  private static final double NEW_USER_FREQUENCY_INDEX = .002;
  private static final double USER_RELOCATION_FREQUENCY = 0.00000;

  private ArrayList<User> users;
  private Map<Long, User> userFilesMap;
  private ArrayList<Location> clusterLocations = null;

  private static UserBasedStream instance;

  public static UserBasedStream get() {
    if (instance == null) {
      instance = new UserBasedStream();
    }
    return instance;
  }

  private UserBasedStream() {
    super();

    users = new ArrayList<User>();

    userFilesMap = new HashMap<Long, User>();
  };

  /**
   * This functionality is not in the constructor since it requires the cluster locations, which are
   * not ready at the time of construction.
   */
  public void instantiateUsers(ArrayList<Location> clusterLocations) {
    this.clusterLocations = clusterLocations;
    for (int i = 0; i < INITIAL_USER_COUNT; i++) {
      users.add(new User(clusterLocations));
    }
    ConsolePanel.get().out("User locations:");
    for (User user : users) {
      ConsolePanel.get().out("    " + user.getHome());
    }
  }

  public void flushUserList() {
    users.clear();
  }

  @Override
  public Request getNextRequest() {
    double randomizationIndex = rand.nextDouble();
    if (randomizationIndex < USER_RELOCATION_FREQUENCY * users.size()) {
      users.get(rand.nextInt(users.size())).setHome(Util.getRandomUrbanLocation(clusterLocations));
    }
    if (randomizationIndex < NEW_USER_FREQUENCY_INDEX / users.size()) {
      users.add(new User(clusterLocations));
    }

    double accessType = rand.nextDouble();
    if (accessType < (double) 1 / 3) {
      return getNextInitializtionRequest();
    } else if (accessType < (double) 2 / 3) {
      long fileId = fileIds.get(rand.nextInt(fileIds.size()));
      File existingFile = new File(Util.getRandomFileSize(), fileId);
      User user = userFilesMap.get(fileId);
      return new UpdateRequest(existingFile, user.getRandomOperationLocation());
    } else {
      long existingFileId = fileIds.get(rand.nextInt(fileIds.size()));
      User user = userFilesMap.get(existingFileId);
      return new ReadRequest(existingFileId, user.getRandomOperationLocation());
    }
  }

  @Override
  public WriteRequest getNextInitializtionRequest() {
    File file = createFreshRandomFile();
    User user = users.get(rand.nextInt(users.size()));
    userFilesMap.put(file.getId(), user);
    return new WriteRequest(file, user.getRandomOperationLocation());
  }

  @Override
  public void printState(boolean verbose) {
    System.out.println(users.size() + " users in the system.");
    if (verbose) {
      for (User user : users) {
        System.out.println("  " + user.getId() + " - " + user.getHome());
      }
    }
  }

  @Override
  public String toString() {
    return "User Based";
  }
}
