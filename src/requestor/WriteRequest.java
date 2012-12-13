package requestor;

import structure.File;
import system.Location;

public class WriteRequest extends Request {
  private File file;

  public WriteRequest(File file, Location location) {
    super(location);
    this.file = file;
  }

  public WriteRequest(File file) {
    super();
    this.file = file;
  }

  public File getFile() {
    return file;
  }

  @Override
  public RequestType getRequestType() {
    return RequestType.WRITE;
  }

}
