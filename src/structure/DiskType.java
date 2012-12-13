package structure;

import java.util.Random;

import system.FssConstants;

public enum DiskType {
  HDD(FssConstants.HDD_CAPACITY,
      FssConstants.HDD_WRITE_SPEED,
      FssConstants.HDD_READ_SPEED,
      FssConstants.HDD_MIN_LATENCY,
      FssConstants.HDD_MAX_LATENCY,
      FssConstants.HDD_WRITES_BEFORE_INSTABILITY),
  SSD(FssConstants.SSD_CAPACITY,
      FssConstants.SSD_WRITE_SPEED,
      FssConstants.SSD_READ_SPEED,
      FssConstants.SSD_MIN_LATENCY,
      FssConstants.SSD_MAX_LATENCY,
      FssConstants.SSD_WRITES_BEFORE_INSTABILITY);

  private static Random rand = new Random();
  private long defaultCapacity;
  private long writeSpeed;
  private long readSpeed;
  private long minLatency;
  private long maxLatency;
  private int writesBeforeInstability;

  private DiskType(long defaultCapacity, long writeSpeed, long readSpeed, long minLatency,
      long maxLatency, int writesBeforeInstability) {
    this.defaultCapacity = defaultCapacity;
    this.writeSpeed = writeSpeed;
    this.readSpeed = readSpeed;
    this.minLatency = minLatency;
    this.maxLatency = maxLatency;
    this.writesBeforeInstability = writesBeforeInstability;
  }

  public long getDefaultCapacity() {
    return defaultCapacity;
  }

  public long getWriteSpeed() {
    return writeSpeed;
  }

  public long getReadSpeed() {
    return readSpeed;
  }

  public long getRandomLatency() {
    return minLatency + Math.abs(rand.nextLong() % (maxLatency - minLatency));
  }

  public int getWritesBeforeInstability() {
    return writesBeforeInstability;
  }
}
