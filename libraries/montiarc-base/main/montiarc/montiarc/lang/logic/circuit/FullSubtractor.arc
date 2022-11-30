/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang.logic.circuit;

import montiarc.lang.logic.gate.Or;

component FullSubtractor {

  // the minuend bit
  port <<sync>> in boolean a;

  // the subtrahend bit
  port <<sync>> in boolean b;

  // the input borrow bit
  port <<sync>> in boolean boIn;

  // the difference of a and b
  port <<sync>> out boolean diff;

  // the output borrow bit
  port <<sync>> out boolean boOut;

  HalfSubtractor hs0;

  a -> hs0.a;
  b -> hs0.b;

  HalfSubtractor hs1;

  boIn -> hs1.a;
  hs0.diff -> hs1.b;
  hs1.diff -> diff;

  Or or;

  hs0.bo -> or.a;
  hs1.bo -> or.b;
  or.q -> boOut;

}
