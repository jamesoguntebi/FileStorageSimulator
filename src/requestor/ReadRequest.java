package requestor;

import system.Location;

public class ReadRequest extends Request {
  private long fileId;

  public ReadRequest(long fileId, Location location) {
    super(location);
    this.fileId = fileId;
  }

  public ReadRequest(long fileId) {
    super();
    this.fileId = fileId;
  }

  public long getFileId() {
    return fileId;
  }

  @Override
  public RequestType getRequestType() {
    return RequestType.READ;
  }
}
