/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang.logic.univ.nor;

import montiarc.lang.logic.gate.Nor;

component Xor {

  port <<sync>> in boolean a,
       <<sync>> in boolean b;
  port <<sync>> out boolean q;

  Nor notA;
  a -> notA.a;
  a -> notA.b;

  Nor notB;
  b -> notB.a;
  b -> notB.b;

  Nor nor1;
  a -> nor1.a;
  b -> nor1.b;

  Nor nor2;
  notA.q -> nor2.a;
  notB.q -> nor2.b;

  Nor nor3;
  nor1.q -> nor3.a;
  nor2.q -> nor3.b;

  nor3.q -> q;
}
