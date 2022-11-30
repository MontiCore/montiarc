/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang.logic.circuit;

import montiarc.lang.logic.gate.Or;

component Encoder8x3 {

  port <<sync>> in boolean a0, a1, a2, a3,
                           a4, a5, a6, a7;
  port <<sync>> out boolean q0, q1, q2;

  // Q0 = OR(A1, A3, A5, A7) = OR(OR(OR(A1, A3), A5), A7)
  Or or13, or135, or1357;

  a1 -> or13.a;
  a3 -> or13.b;
  a5 -> or135.a;
  a7 -> or1357.a;
  or13.q -> or135.b;
  or135.q -> or1357.b;
  or1357.q -> q0;

  // Q1 = OR(A2, A3, A6, A7) = OR(OR(OR(A2, A3), A6), A7)
  Or or23, or236, or2367;

  a2 -> or23.a;
  a3 -> or23.b;
  a6 -> or236.a;
  a7 -> or2367.a;
  or23.q -> or236.b;
  or236.q -> or2367.b;
  or2367.q -> q1;

  // Q2 = OR(A4, A5, A6, A7) = OR(OR(OR(A4, A5), A6), A7)
  Or or45, or456, or4567;

  a4 -> or45.a;
  a5 -> or45.b;
  a6 -> or456.a;
  a7 -> or4567.a;
  or45.q -> or456.b;
  or456.q -> or4567.b;
  or4567.q -> q2;

}
