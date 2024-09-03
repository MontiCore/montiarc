/* (c) https://github.com/MontiCore/monticore */
package factory.company;

import factory.Factory.*;

component Warehouse {
  port in Material materialIn,
       in Order order;
  port out Material materialOut;

  automaton {
    // TODO: Manage material in and out flow
    initial state s;
  }
}