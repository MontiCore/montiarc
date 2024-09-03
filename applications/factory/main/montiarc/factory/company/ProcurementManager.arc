/* (c) https://github.com/MontiCore/monticore */
package factory.company;

import factory.Factory.*;

component ProcurementManager {
  port in Material material;
  port out PurchaseOrder purchase;

  automaton {
    // TODO: Order material on a regular basis and additionally if order volume can not be fulfilled
    initial state s;
  }

}