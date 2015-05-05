package com.java7developer.chapter3;

/**
 * Code for listing 3_3
 */
public class AgentFinderFactory {

  private static AgentFinderFactory singleton;

  private AgentFinderFactory() {
  }

  public static AgentFinderFactory getInstance() {
    if (singleton == null) {
      singleton = new AgentFinderFactory();
    }
    return singleton;
  }

  public AgentFinder getAgentFinder(String agentType) {
    AgentFinder finder = null;
    switch (agentType) {
    case "spreadsheet":
      finder = new SpreadsheetAgentFinder();
      break;
    case "webservice":
      finder = new WebServiceAgentFinder();
      break;
    default:
      finder = new WebServiceAgentFinder();
      break;
    }
    return finder;
  }
}
