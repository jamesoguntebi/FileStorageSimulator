package requestor.user_based;

import java.util.ArrayList;
import java.util.Random;

import system.Location;
import system.Util;

public class User {
  private static Random rand = new Random();
  private static final int DEGREES_TO_MILES = 70;
  private static final double USER_OPERATION_RADIUS = 0.5 / DEGREES_TO_MILES;
  private static int idCounter = 0;

  private Location home;
  private int id;

  public User(ArrayList<Location> clusterLocations) {
    id = ++idCounter;
    if (clusterLocations != null && !clusterLocations.isEmpty()) {
      home = Util.getRandomUrbanLocation(clusterLocations);
    } else {
      home = Util.getRandomLocation();
    }
  }

  public Location getRandomOperationLocation() {
    double latDelta = - USER_OPERATION_RADIUS + rand.nextDouble() * 2 * USER_OPERATION_RADIUS;
    double lngDelta = - USER_OPERATION_RADIUS + rand.nextDouble() * 2 * USER_OPERATION_RADIUS;
    double newLat = home.getLat() + latDelta;
    newLat = Math.max(-180, Math.min(180, newLat));
    double newLng = home.getLng() + lngDelta;
    newLng = Math.max(-90, Math.min(90, newLng));
    return new Location(newLat, newLng);
  }

  public Location getHome() {
    return home;
  }

  public void setHome(Location home) {
    this.home = home;
  }

  public int getId() {
    return id;
  }
}
