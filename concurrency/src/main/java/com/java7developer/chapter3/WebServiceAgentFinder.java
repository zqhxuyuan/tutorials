package com.java7developer.chapter3;

import java.util.ArrayList;
import java.util.List;

/**
 * Code for listing 3_1
 */
public class WebServiceAgentFinder implements AgentFinder {

  /**
   * This method returns an empty list of agents for compilation sake
   * 
   * @return An empty list of Agents
   */
  @Override
  public List<Agent> findAllAgents() {
    List<Agent> agents = new ArrayList<>();
    // Lots of RESTFul based implementation would go here
    return agents;
  }

}
