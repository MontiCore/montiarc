/* (c) https://github.com/MontiCore/monticore */
package factory.company;

import factory.Factory.*;

component Production {
  port in Order order,
       in Material material;
  port out ConstructionPart producedPart;

  ProductionManager manager;
  Machine machine;
  // TODO: Create machine for every processing type

  order -> manager.order;
  order -> machine.order;
  material -> machine.material;

  machine.part -> producedPart;

  automaton {
    // TODO: Manage processing steps
    initial state s;
  }

}