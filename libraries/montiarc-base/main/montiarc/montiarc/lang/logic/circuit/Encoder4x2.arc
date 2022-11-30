/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang.logic.circuit;

import montiarc.lang.logic.gate.Or;

component Encoder4x2 {

  port <<sync>> in boolean a0, a1, a2, a3;
  port <<sync>> out boolean q0, q1;

  // Q0 = OR(A1, A2, A3) = OR(OR(A1, A2), A3)
  Or or13, or23;

  a1 -> or13.a;
  a3 -> or13.b;
  or13.q -> q0;

  a2 -> or23.a;
  a3 -> or23.b;
  or23.q -> q1;

}
