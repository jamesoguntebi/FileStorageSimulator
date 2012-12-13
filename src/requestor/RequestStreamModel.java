package requestor;

import java.util.ArrayList;

import requestor.user_based.UserBasedStream;
import structure.File;
import system.Util;

public abstract class RequestStreamModel {
  protected ArrayList<Long> fileIds;

  protected RequestStreamModel() {
    fileIds = new ArrayList<Long>();
  }

  abstract public Request getNextRequest();
  abstract public WriteRequest getNextInitializtionRequest();
  abstract public void printState(boolean verbose);

  public abstract String toString();

  protected File createFreshRandomFile() {
    File freshFile = new File(Util.getRandomFileSize());
    fileIds.add(freshFile.getId());
    return freshFile;
  }

  public void flushFileHistory() {
    fileIds.clear();
  }

  public static RequestStreamModel[] getRequestStreamModels() {
    return new RequestStreamModel[] {
      RandomRequestor.get(),
      CommonIOStream.get(),
      NetworkComputationStream.get(),
      UserBasedStream.get()
    };
  }
}
