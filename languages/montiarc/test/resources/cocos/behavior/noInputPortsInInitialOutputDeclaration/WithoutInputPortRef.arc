/* (c) https://github.com/MontiCore/monticore */
package noInputPortsInInitialOutputDeclaration;

import behavior.noInputPortsInInitialOutputDeclaration.helpers.*;

/**
 * Valid model
 */
component WithoutInputPortRef (int param) extends Parent {
  port in int ownIn;
  port out int ownOut1, ownOut2, ownOut3;

  int field = 0;

  automaton {
    initial {
      ownOut1 = param;
      ownOut2 = field * 2;
      ownOut3 = (field / (param != 0 ? param : 1)) + 2;
    } state A;
  }
}
