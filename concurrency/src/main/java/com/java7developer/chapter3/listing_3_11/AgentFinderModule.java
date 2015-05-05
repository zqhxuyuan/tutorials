package com.java7developer.chapter3.listing_3_11;

import com.google.inject.AbstractModule;
import com.java7developer.chapter3.AgentFinder;

/**
 * Code for listing 3_11
 */
public class AgentFinderModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(AgentFinder.class).toProvider(AgentFinderProvider.class);
  }
}