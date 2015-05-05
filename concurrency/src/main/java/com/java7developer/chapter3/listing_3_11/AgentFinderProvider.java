package com.java7developer.chapter3.listing_3_11;

import com.google.inject.Provider;
import com.java7developer.chapter3.AgentFinder;
import com.java7developer.chapter3.SpreadsheetAgentFinder;

/**
 * Code for listing 3_11
 */
public class AgentFinderProvider implements Provider<AgentFinder> {

  @Override
  public AgentFinder get() {
    SpreadsheetAgentFinder finder = new SpreadsheetAgentFinder();
    finder.setType("Excel 97");
    finder.setPath("C:/temp/agents.xls");
    return finder;
  }
}
