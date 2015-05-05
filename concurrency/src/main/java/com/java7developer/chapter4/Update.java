package com.java7developer.chapter4;

public final class Update implements Comparable<Update> {

  private final Author author;
  private final String updateText;
  private final long createTime;

  public Author getAuthor() {
    return author;
  }

  public String getUpdateText() {
    return updateText;
  }

  private Update(Builder b_) {
    author = b_.author;
    updateText = b_.updateText;
    createTime = b_.createTime;
  }

  public static class Builder implements ObjBuilder<Update> {
    public long createTime;
    private Author author;
    private String updateText;

    public Builder author(Author author_) {
      author = author_;
      return this;
    }

    public Builder updateText(String updateText_) {
      updateText = updateText_;
      return this;
    }

    public Builder createTime(long createTime_) {
      createTime = createTime_;
      return this;
    }

    public Update build() {
      return new Update(this);
    }
  }

  public int compareTo(Update other_) {
    if (null == other_)
      throw new NullPointerException();

    return (int) (other_.createTime - this.createTime);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((author == null) ? 0 : author.hashCode());
    result = prime * result + (int) (createTime ^ (createTime >>> 32));
    result = prime * result
        + ((updateText == null) ? 0 : updateText.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Update other = (Update) obj;
    if (author == null) {
      if (other.author != null)
        return false;
    } else if (!author.equals(other.author))
      return false;
    if (createTime != other.createTime)
      return false;
    if (updateText == null) {
      if (other.updateText != null)
        return false;
    } else if (!updateText.equals(other.updateText))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Update [author=" + author + ", updateText=" + updateText
        + ", createTime=" + createTime + "]";
  }

}
