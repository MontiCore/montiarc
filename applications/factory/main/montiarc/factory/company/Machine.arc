/* (c) https://github.com/MontiCore/monticore */
package factory.company;

import factory.Factory.*;

component Machine {
  port in Material material,
       in Order order;
  port out ConstructionPart part;

  // Material materialBuffer;

  automaton {
    initial state s;

    /*
    s -> s [material != null] / {
      materialBuffer = material;
    };
    */
  }
}