/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang.logic.circuit;

import montiarc.lang.logic.gate.And;
import montiarc.lang.logic.gate.Not;
import montiarc.lang.logic.gate.Or;

component Mux4x1 {

  // the data input bits
  port <<sync>> in boolean i0, i1, i2, i3;

  // the selection input bits
  port <<sync>> in boolean s0, s1;

  // the selected bit
  port <<sync>> out boolean o;

  Not notS0, notS1;

  s0 -> notS0.a;
  s1 -> notS1.a;

  And sI0, fI0;

  notS0.q -> sI0.a;
  notS1.q -> sI0.b;
  sI0.q -> fI0.a;
  i0 -> fI0.b;

  And sI1, fI1;

  notS0.q -> sI1.a;
  s1 -> sI1.b;
  sI1.q -> fI1.a;
  i1 -> fI1.b;

  And sI2, fI2;

  s0 -> sI2.a;
  notS1.q -> sI2.b;
  sI2.q -> fI2.a;
  i2 -> fI2.b;

  And sI3, fI3;

  s0 -> sI3.a;
  s1 -> sI3.b;
  sI3.q -> fI3.a;
  i3 -> fI3.b;

  Or or01, or23;

  fI0.q -> or01.a;
  fI1.q -> or01.b;

  fI2.q -> or23.a;
  fI3.q -> or23.b;

  Or or;

  or01.q -> or.a;
  or23.q -> or.b;

  or.q -> o;

}
