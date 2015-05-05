package com.java7developer.chapter4;

public interface SimpleMicroBlogNode {
  void propagateUpdate(Update upd_, SimpleMicroBlogNode backup_);

  void confirmUpdate(SimpleMicroBlogNode other_, Update update_);

  String getIdent();
}
