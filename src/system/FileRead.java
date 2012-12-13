package system;

import structure.File;

public class FileRead {
  public File file;
  public long time;

  public FileRead(File file, long time) {
    this.file = file;
    this.time = time;
  }
}