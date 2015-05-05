package com.java7developer.chapter3;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Code for listing 3_6
 */
class MurmurMessage {
  boolean someGlobalCondition = true;

  @Inject
  MurmurMessage(Provider<Message> messageProvider) {
    Message msg1 = messageProvider.get();
    if (someGlobalCondition) {
      Message copyOfMsg1 = messageProvider.get();
    }
    // Do stuff with msg1 and copyOfMsg1
  }

  private class Message {
  }
}