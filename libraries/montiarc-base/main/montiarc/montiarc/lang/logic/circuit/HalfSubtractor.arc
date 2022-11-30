/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang.logic.circuit;

import montiarc.lang.logic.gate.And;
import montiarc.lang.logic.gate.Not;
import montiarc.lang.logic.gate.Xor;

component HalfSubtractor {

  // the minuend bit
  port <<sync>> in boolean a;

  // the subtrahend bit
  port <<sync>> in boolean b;

  // the difference of a and b
  port <<sync>> out boolean diff;

  // the borrow bit
  port <<sync>> out boolean bo;

  Xor xor;

  a -> xor.a;
  b -> xor.b;
  xor.q -> diff;

  Not notA;

  a -> notA.a;

  And and;

  notA.q -> and.a;
  b -> and.b;
  and.q -> bo;

}
