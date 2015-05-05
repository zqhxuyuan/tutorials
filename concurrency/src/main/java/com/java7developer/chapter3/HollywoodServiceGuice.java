package com.java7developer.chapter3;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;

/**
 * Code for listing 3_7
 */
public class HollywoodServiceGuice {

  private AgentFinder finder = null;

  @Inject
  public HollywoodServiceGuice(AgentFinder agentFinder) {
    this.finder = agentFinder;
  }

  public List<Agent> getFriendlyAgents() {
    List<Agent> agents = finder.findAllAgents();
    List<Agent> friendlyAgents = filterAgents(agents, "Java Developers");
    return friendlyAgents;
  }

  public List<Agent> filterAgents(List<Agent> agents, String agentType) {
    List<Agent> filteredAgents = new ArrayList<>();
    for (Agent agent : agents) {
      if (agent.getType().equals("Java Developers")) {
        filteredAgents.add(agent);
      }
    }
    return filteredAgents;
  }
}