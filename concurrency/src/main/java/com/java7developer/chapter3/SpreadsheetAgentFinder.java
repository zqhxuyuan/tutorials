package com.java7developer.chapter3;

import java.util.ArrayList;
import java.util.List;

/**
 * Code for listing 3_1
 */
public class SpreadsheetAgentFinder implements AgentFinder {

  /** The type of spreadsheet we are dealing with */
  private String type;

  /** The location of the spreadsheet */
  private String path;

  /**
   * This method returns an empty list of agents for compilation sake
   * 
   * @return An empty list of Agents
   */
  @Override
  public List<Agent> findAllAgents() {
    List<Agent> agents = new ArrayList<>();
    // Lots of POI based implementation would go here
    return agents;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void setPath(String path) {
    this.path = path;
  }

}