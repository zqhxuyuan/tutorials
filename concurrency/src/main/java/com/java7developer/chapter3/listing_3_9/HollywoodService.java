package com.java7developer.chapter3.listing_3_9;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.java7developer.chapter3.AgentFinder;

/**
 * Code for listing 3_9
 */
public class HollywoodService {

  private AgentFinder finder = null;

  @Inject
  public HollywoodService(@Named("primary") AgentFinder finder) {
    this.finder = finder;
  }
}
