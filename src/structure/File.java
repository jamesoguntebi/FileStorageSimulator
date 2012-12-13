package structure;

import java.util.Date;

public class File {
  /**
   * Total number of files in system.
   */
  private static long fileCount = 0;

  /**
   * Unique identifier for file.
   */
  private long id;

  /**
   * Size of file in bytes.
   */
  private long size;

  /**
   * Date file was created.
   */
  private Date created;

  /**
   * Date file was most recently modified.
   */
  private Date modified;

  /**
   * Constructor.
   */
  public File(long size, long id) {
    this.id = id;
    this.size = size;
    created = new Date();
    modified = (Date) created.clone();
  }

  /**
   * Constructor.
   */
  public File(long size) {
    this(size, ++fileCount);
  }

  @Override
  public String toString() {
    return "_F" + id + "_; " + size + " bytes; created " + created + "; modified " + modified;
  }

  @Override
  public int hashCode() {
    return (int) id;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof File)) {
      return false;
    }

    return ((File) obj).id == id;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public long getSize() {
    return size;
  }

  public void setSize(long size) {
    this.size = size;
  }

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  public Date getModified() {
    return modified;
  }

  public void setModified(Date modified) {
    this.modified = modified;
  }
}
