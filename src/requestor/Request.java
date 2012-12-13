package requestor;

import system.Location;
import system.Util;

public abstract class Request {
  private Location location;

  protected Request(Location location) {
    this.location = location;
  }

  protected Request() {
    this(Util.getRandomLocation());
  }

  public Location getLocation() {
    return location;
  }

  abstract public RequestType getRequestType();
}
