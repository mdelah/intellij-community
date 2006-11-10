package com.intellij.localvcs;

import java.io.IOException;

public class CreateFileChange extends Change {
  private Path myPath;
  private String myContent;
  private Integer myId;
  private IdPath myAffectedEntryIdPath;

  public CreateFileChange(Integer id, Path path, String content) {
    myPath = path;
    myContent = content;
    myId = id;
  }

  public CreateFileChange(Stream s) throws IOException {
    myPath = s.readPath();
    myContent = s.readString();
  }

  @Override
  public void write(Stream s) throws IOException {
    s.writePath(myPath);
    s.writeString(myContent);
  }

  public Path getPath() {
    return myPath;
  }

  public String getContent() {
    return myContent;
  }

  @Override
  public void applyTo(RootEntry root) {
    root.doCreateFile(myId, myPath, myContent);
    myAffectedEntryIdPath = root.getEntry(myPath).getIdPath();
  }

  @Override
  public void revertOn(RootEntry root) {
    root.doDelete(myPath);
  }

  @Override
  protected IdPath getAffectedEntryIdPath() {
    return myAffectedEntryIdPath;
  }
}
