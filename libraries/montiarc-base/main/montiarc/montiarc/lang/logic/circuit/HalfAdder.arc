/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang.logic.circuit;

import montiarc.lang.logic.gate.And;
import montiarc.lang.logic.gate.Xor;

component HalfAdder {

  // the augend bit
  port <<sync>> in boolean a;

  // the addend bit
  port <<sync>> in boolean b;

  // the sum of a and b
  port <<sync>> out boolean sum;

  // the carry bit
  port <<sync>> out boolean ca;

  Xor xor;

  a -> xor.a;
  b -> xor.b;
  xor.q -> sum;

  And and;

  a -> and.a;
  a -> and.b;
  and.q -> ca;

}
