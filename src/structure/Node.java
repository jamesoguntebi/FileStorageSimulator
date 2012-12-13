package structure;

import java.util.HashMap;
import java.util.Random;

import system.FileRead;
import system.FssConstants;
import ui.ConsolePanel;

public class Node {
  private static final Random random = new Random();
  private long id;
  private long capacity;
  private long freeSpace;
  private HashMap<Long, File> data;
  private DiskType diskType;
  private boolean isHealthy;
  private int writeCount;

  public Node(long id, DiskType diskType) {
    this.id = id;
    this.capacity = diskType.getDefaultCapacity();
    this.diskType = diskType;
    freeSpace = capacity;
    data = new HashMap<Long, File>();
    isHealthy = true;
    writeCount = 0;
  }

  public long freeSpace() {
    return freeSpace;
  }

  public double getUsage() {
    return (double) (capacity - freeSpace) / capacity;
  }

  public long store(File file) {
    if (!isHealthy) {
      return -1;
    }
    if (writeCount > diskType.getWritesBeforeInstability()) {
      double failProb =
          1 - Math.pow(Math.E,
                       (1 - (double) writeCount / diskType.getWritesBeforeInstability()) / 100000);
      if (random.nextDouble() < failProb) {
        fail();
//        ConsolePanel.get().out("        Node " + id + " failed.");
        return -1;
      }
    }
    long newSize = file.getSize();
    long oldSize = data.containsKey(file.getId()) ? data.get(file.getId()).getSize() : 0;
    if (freeSpace > (newSize - oldSize)) {
      writeCount++;
      freeSpace -= (newSize - oldSize);
      data.put(file.getId(), file);
      return diskType.getRandomLatency() + file.getSize() / diskType.getWriteSpeed();
    }

    return -1;
  }

  public FileRead read(long fileId) {
    File f = data.get(fileId);
    if (f == null)
      return null;
    return new FileRead(f, diskType.getRandomLatency() + f.getSize() / diskType.getReadSpeed());
  }

  public void delete(File file) {
    File removedFile = data.remove(file.getId());
    if (removedFile != null) {
      freeSpace += removedFile.getSize();
    }
  }

  public void printState(String indentation, boolean fileDetails) {
    long freeSpace = freeSpace();
    String usage = String.format("%.2f", (double) (capacity - freeSpace) / capacity * 100);
    ConsolePanel.get().out(indentation + "_N" + id + "_; " + data.size() + " files. "
        + (freeSpace / FssConstants.MB_TO_B) + " MB free of " + (capacity / FssConstants.MB_TO_B)
        + " MB (" + usage + "% used).");
    if (fileDetails) {
      for (File f : data.values()) {
        ConsolePanel.get().out(indentation + "    " + f);
      }
    }
  }

  public HashMap<Long, File> getData() {
    return data;
  }

  public void setData(HashMap<Long, File> data) {
    this.data = data;
  }

  public long getId() {
    return id;
  }

  public boolean isHealthy() {
    return isHealthy;
  }

  public void setHealthy(boolean healthy) {
    isHealthy = healthy;
  }

  public void fail() {
    setHealthy(false);
  }
}
