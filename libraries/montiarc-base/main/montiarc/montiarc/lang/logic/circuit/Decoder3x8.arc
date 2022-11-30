/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang.logic.circuit;

import montiarc.lang.logic.gate.And;
import montiarc.lang.logic.gate.Or;
import montiarc.lang.logic.gate.Not;

component Decoder3x8 {

  port <<sync>> in boolean a0, a1, a2;
  port <<sync>> out boolean q0, q1, q2, q3,
                            q4, q5, q6, q7;

  Not notA0, notA1, notA2;

  a0 -> notA0.a;
  a1 -> notA1.a;
  a2 -> notA2.a;

  // QO = AND(NOT(A0), NOT(A1), NOT(A2)) = AND(AND(NOT(A0), NOT(A1)), NOT(A2))
  And andQ01, andQ02;

  notA0.q -> andQ01.a;
  notA1.q -> andQ01.b;
  notA2.q -> andQ02.a;
  andQ01.q -> andQ02.b;
  andQ02.q -> q0;

  // Q1 = AND(NOT(A0), NOT(A1), A2) = AND(AND(NOT(A0), NOT(A1)), A2)
  And andQ11, andQ12;

  notA0.q -> andQ11.a;
  notA1.q -> andQ11.b;
  a2 -> andQ12.a;
  andQ11.q -> andQ12.b;
  andQ12.q -> q1;

  // Q2 = AND(NOT(A0), A1, NOT(A2)) = AND(AND(NOT(A0), A1), NOT(A2))
  And andQ21, andQ22;

  notA0.q -> andQ21.a;
  a1 -> andQ21.b;
  notA2.q -> andQ22.a;
  andQ21.q -> andQ22.b;
  andQ22.q -> q2;

  // Q3 = AND(NOT(A0), A1, A2) = AND(AND(NOT(A0), A1), A2)
  And andQ31, andQ32;

  notA0.q -> andQ31.a;
  a1 -> andQ31.b;
  a2 -> andQ32.a;
  andQ31.q -> andQ32.b;
  andQ32.q -> q3;

  // Q4 = AND(A0, NOT(A1), NOT(A2)) = AND(AND(A0, NOT(A1)), NOT(A2))
  And andQ41, andQ42;

  a0 -> andQ41.a;
  notA1.q -> andQ41.b;
  notA2.q -> andQ42.a;
  andQ41.q -> andQ42.b;
  andQ42.q -> q4;

  // Q5 = AND(A0, NOT(A1), A2) = AND(AND(A0, NOT(A1)), A2)
  And andQ51, andQ52;

  a0 -> andQ51.a;
  notA1.q -> andQ51.b;
  a2 -> andQ52.a;
  andQ51.q -> andQ52.b;
  andQ52.q -> q5;

  // Q6 = AND(A0, A1, NOT(A2)) = AND(AND(A0, A1), NOT(A2))
  And andQ61, andQ62;

  a0 -> andQ61.a;
  a1 -> andQ61.b;
  notA2.q -> andQ62.a;
  andQ61.q -> andQ62.b;
  andQ62.q -> q6;

  // Q7 = AND(A0, A1, A2) = AND(AND(A0, A1), A2)
  And andQ71, andQ72;

  a0 -> andQ71.a;
  a1 -> andQ71.b;
  a2 -> andQ72.a;
  andQ71.q -> andQ72.b;
  andQ72.q -> q7;

}
