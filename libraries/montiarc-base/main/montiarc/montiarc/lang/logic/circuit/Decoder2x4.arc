/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang.logic.circuit;

import montiarc.lang.logic.gate.And;
import montiarc.lang.logic.gate.Or;
import montiarc.lang.logic.gate.Not;

component Decoder2x4 {

  port <<sync>> in boolean a0, a1;
  port <<sync>> out boolean q0, q1, q2, q3;

  Not notA0, notA1;

  a0 -> notA0.a;
  a1 -> notA1.a;

  // Q0 = AND(NOT(A0), NOT(A1))
  And andQ0;

  notA0.q -> andQ0.a;
  notA1.q -> andQ0.b;
  andQ0.q -> q0;

  // Q1 = AND(NOT(A0), A1)
  And andQ1;

  notA0.q -> andQ1.a;
  a1 -> andQ1.b;
  andQ1.q -> q1;

  // Q2 = AND(A0, NOT(A1))
  And andQ2;

  a0 -> andQ2.a;
  notA1.q -> andQ2.b;
  andQ2.q -> q2;

  // Q3 = AND(A0, A1)
  And andQ3;

  a0 -> andQ3.a;
  a1 -> andQ3.b;
  andQ3.q -> q3;

}
