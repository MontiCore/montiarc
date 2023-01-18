/* (c) https://github.com/MontiCore/monticore */
package noInputPortsInInitialOutputDeclaration;

import behavior.noInputPortsInInitialOutputDeclaration.helpers.*;

/**
 * Invalid model
 */
component WithInputPortRef (int param) extends Parent {
  port in int ownIn;
  port out int ownOut1, ownOut2, ownOut3;

  automaton {
    initial {
      ownOut1 = param;
      ownOut2 = parentIn * 2;
      ownOut3 = (ownIn / 4) + 2;    // <- reading ownIn is illegal in the initialization
    } state A;
  }
}
