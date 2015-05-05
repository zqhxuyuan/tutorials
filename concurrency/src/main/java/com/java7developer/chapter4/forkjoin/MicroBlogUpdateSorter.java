package com.java7developer.chapter4.forkjoin;

import com.java7developer.chapter4.Update;
import java.util.Arrays;
import java.util.concurrent.RecursiveAction;

public class MicroBlogUpdateSorter extends RecursiveAction {
  private static final int SMALL_ENOUGH = 32;
  private final Update[] updates;
  private final int start, end;
  private final Update[] result;

  public MicroBlogUpdateSorter(Update[] updates_) {
    this(updates_, 0, updates_.length);
  }

  public MicroBlogUpdateSorter(Update[] upds_, int startPos_, int endPos_) {
    start = startPos_;
    end = endPos_;
    updates = upds_;
    result = new Update[updates.length];
  }

  private void merge(MicroBlogUpdateSorter left_, MicroBlogUpdateSorter right_) {
    int i = 0;
    int lCt = 0;
    int rCt = 0;
    while (lCt < left_.size() && rCt < right_.size()) {
      result[i++] = (left_.result[lCt].compareTo(right_.result[rCt]) < 0) ? left_.result[lCt++]
          : right_.result[rCt++];
    }
    while (lCt < left_.size())
      result[i++] = left_.result[lCt++];
    while (rCt < right_.size())
      result[i++] = right_.result[rCt++];
  }

  public int size() {
    return end - start;
  }

  public Update[] getResult() {
    return result;
  }

  @Override
  protected void compute() {
    if (size() < SMALL_ENOUGH) {
      System.arraycopy(updates, start, result, 0, size());
      Arrays.sort(result, 0, size());
    } else {
      int mid = size() / 2;
      MicroBlogUpdateSorter left = new MicroBlogUpdateSorter(updates, start,
          start + mid);
      MicroBlogUpdateSorter right = new MicroBlogUpdateSorter(updates, start
          + mid, end);
      invokeAll(left, right);
      merge(left, right);
    }
  }
}