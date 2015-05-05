package com.java7developer.chapter3;

import java.util.ArrayList;
import java.util.List;

/**
 * Code for listing 3_4
 */
public class HollywoodServiceWithDI {

  public static List<Agent> getFriendlyAgents(AgentFinder finder) {
    List<Agent> agents = finder.findAllAgents();
    List<Agent> friendlyAgents = filterAgents(agents, "Java Developers");
    return friendlyAgents;
  }

  public static List<Agent> filterAgents(List<Agent> agents, String agentType) {
    List<Agent> filteredAgents = new ArrayList<>();
    for (Agent agent : agents) {
      if (agent.getType().equals("Java Developers")) {
        filteredAgents.add(agent);
      }
    }
    return filteredAgents;
  }
}
