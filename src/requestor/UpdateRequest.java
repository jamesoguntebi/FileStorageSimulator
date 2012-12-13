package requestor;

import structure.File;
import system.Location;

public class UpdateRequest extends WriteRequest {

  public UpdateRequest(File file, Location location) {
    super(file, location);
  }

  public UpdateRequest(File file) {
    super(file);
  }

  @Override
  public RequestType getRequestType() {
    return RequestType.UPDATE;
  }

}
