package com.java7developer.chapter3;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.java7developer.chapter3.listing_3_9.AgentFinderModule;
import java.util.List;

/**
 * Code for listing 3_8
 */
public class HollywoodServiceClient {

  public static void main(String[] args) {
    Injector injector = Guice.createInjector(new AgentFinderModule());
    HollywoodServiceGuice hollywoodService = injector
        .getInstance(HollywoodServiceGuice.class);
    List<Agent> agents = hollywoodService.getFriendlyAgents();
    // Do stuff with agents.
  }

}
