package system;

import java.util.ArrayList;
import java.util.Random;

import structure.File;

public class Util {
  private static final double LN_2 = Math.log(2);
  private static final int DEGREES_TO_MILES = 70;
  private static final int URBAN_RADIUS_MILES = 70;
  private static final double URBAN_RADIUS = URBAN_RADIUS_MILES / DEGREES_TO_MILES;
  private static final double END_SPEEDUP_COEF = Math.log(FssConstants.LINK_BANDWIDTH
      / FssConstants.PACKET_SIZE)
      / LN_2;

  private static Random rand = new Random();
  private static long totalNetworkTime = 0;
  private static int networkCalculations = 0;

  public static File generateFreshFile() {
    return new File(getRandomFileSize());
  }

  public static long getRandomFileSize() {
    return FssConstants.MIN_FILE_SIZE
        + Math.abs(rand.nextLong() % (FssConstants.MAX_FILE_SIZE - FssConstants.MIN_FILE_SIZE));
  }

  public static long getLargeRandomFileSize() {
    return (long) (FssConstants.MAX_FILE_SIZE);// * .95
//        + Math.abs(rand.nextLong() % (FssConstants.MAX_FILE_SIZE - FssConstants.MAX_FILE_SIZE * .95)));
  }

  public static Location getRandomLocation() {
    return new Location(-180.0 + 360 * rand.nextDouble(), -90.0 + 180 * rand.nextDouble());
  }

  public static Location getRandomUrbanLocation(ArrayList<Location> clusterLocations) {
    try {
      Location clusterLoc = clusterLocations.get(rand.nextInt(clusterLocations.size()));
      double radius = rand.nextDouble() * URBAN_RADIUS;
      double angle = rand.nextDouble() * 2 * Math.PI;
      double newLat = clusterLoc.getLat() + radius * Math.cos(angle);
      newLat = Math.max(-180, Math.min(180, newLat));
      double newLng = clusterLoc.getLng() + radius * Math.sin(angle);
      newLng = Math.max(-90, Math.min(90, newLng));
      Location answer = new Location(newLat, newLng);
      return answer;
    } catch (ArrayIndexOutOfBoundsException e) {
      return getRandomLocation();
    }
  }

  public static long networkTime(long bytes, double distance) {
    long propogationDelay = (long) distance / FssConstants.LINK_SPEED;
    long rtt = Math.max(1, 2 * propogationDelay);

    // EXPONENTIAL SPEEDUP SIMULATION
    double endSpeedup = rtt * END_SPEEDUP_COEF;
    double speedupMax = FssConstants.PACKET_SIZE * rtt * (Math.pow(2, endSpeedup / rtt) - 1) / LN_2;
    long transmissionDelay;
    if (speedupMax > bytes) {
      transmissionDelay = (long) (rtt
          * Math.log(1 + (LN_2 * bytes) / (FssConstants.PACKET_SIZE * rtt)) / LN_2);
    } else {
      transmissionDelay = (long) (endSpeedup + (bytes - speedupMax) / FssConstants.LINK_BANDWIDTH);
    }

    long networkTime = propogationDelay + transmissionDelay;
    totalNetworkTime += networkTime;
    networkCalculations++;
    return networkTime;
  }

  public static long avgNetworkTime() {
    return totalNetworkTime / networkCalculations;
  }

  public static void flushStatistics() {
    totalNetworkTime = 0;
    networkCalculations = 0;
  }
}
