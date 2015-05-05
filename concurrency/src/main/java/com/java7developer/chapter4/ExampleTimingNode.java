package com.java7developer.chapter4;

import java.util.HashMap;
import java.util.Map;

public class ExampleTimingNode implements SimpleMicroBlogNode {

  private final String identifier;

  private final Map<Update, Long> arrivalTime = new HashMap<>();

  public ExampleTimingNode(String identifier_) {
    identifier = identifier_;
  }

  @Override
  public synchronized void propagateUpdate(Update upd_,
      SimpleMicroBlogNode backup_) {
    long currentTime = System.currentTimeMillis();
    arrivalTime.put(upd_, currentTime);
  }

  @Override
  public synchronized void confirmUpdate(SimpleMicroBlogNode other_,
      Update update_) {
    Long timeRecvd = arrivalTime.get(update_);
    System.out.println("Recvd confirm: " + update_.getUpdateText() + " from "
        + other_.getIdent());
  }

  @Override
  public String getIdent() {
    return identifier;
  }

}