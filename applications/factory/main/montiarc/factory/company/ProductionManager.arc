/* (c) https://github.com/MontiCore/monticore */
package factory.company;

import factory.Factory.*;

component ProductionManager {
  port in Order order;

  automaton {
    // TODO: Manage distribution of parts to different machines
    initial state s;
  }
}