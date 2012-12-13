package system;

public class FssConstants {
  public static final long MB_TO_B = 1024 * 1024;

  public static final double MIN_CLUSTER_DISTANCE = 1000;

  public static final long HDD_CAPACITY = 1099511627776L; // 1 TB
  public static final long HDD_WRITE_SPEED = 59768832; // 57 MB/s
  public static final long HDD_READ_SPEED = 65011712; // 62 MB/s
  public static final long HDD_MIN_LATENCY = 150;
  public static final long HDD_MAX_LATENCY = 2000;
  public static final int HDD_WRITES_BEFORE_INSTABILITY = 2000;

  public static final long SSD_CAPACITY = 268435456000L; // 250 GB
  public static final long SSD_WRITE_SPEED = 685768704; // 654 MB/s
  public static final long SSD_READ_SPEED = 746586112; // 712 MB/s
  public static final long SSD_MIN_LATENCY = 1;
  public static final long SSD_MAX_LATENCY = 60;
  public static final int SSD_WRITES_BEFORE_INSTABILITY = 2000;

  public static final long MIN_FILE_SIZE = 4096; // 4 KB
  public static final long MAX_FILE_SIZE = 1073741824L; // 1 GB

  /**
   * Speed of light in miles per millisecond.
   */
  private static final long SPEED_OF_LIGHT = 186;
  public static final long LINK_SPEED = SPEED_OF_LIGHT * 55 / 100;
  private static final long GIGABIT = 125000000; 
  public static final long LINK_BANDWIDTH = GIGABIT / 2;
  public static final long PACKET_SIZE = 1500; // 1500B
}
