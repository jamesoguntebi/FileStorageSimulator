package system;

public class Location {
  private double lat;
  private double lng;

  public Location(double lat, double lng) {
    this.lat = lat;
    this.lng = lng;
  }

  /**
   * In miles.
   * 
   * @param loc
   * @return
   */
  public double distance(Location loc) {
    double lat2 = loc.lat;
    double lng2 = loc.lng;
    double sin1 = Math.sin((lat2 - lat) / 2);
    double sin2 = Math.sin((lng2 - lng) / 2);
    double dist = 2 * 3959
        * Math.sqrt(sin1 * sin1 + Math.cos(lat) * Math.cos(lat2) * sin2 * sin2);
    return dist;
  }

  public double getLat() {
    return lat;
  }

  public double getLng() {
    return lng;
  }

  public String toString() {
    return String.format("(%.2f, %.2f)", lat, lng);
  }
}
