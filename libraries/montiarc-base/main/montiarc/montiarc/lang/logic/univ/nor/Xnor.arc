/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang.logic.univ.nor;

import montiarc.lang.logic.gate.Nor;

component Xnor {

  port <<sync>> in boolean a,
       <<sync>> in boolean b;
  port <<sync>> out boolean q;

  Nor norAB;
  a -> norAB.a;
  b -> norAB.b;

  Nor norA;
  a -> norA.a;
  norAB.q -> norA.b;

  Nor norB;
  b -> norB.a;
  norAB.q -> norB.b;

  Nor nor;
  norA.q -> nor.a;
  norB.q -> nor.b;

  nor.q -> q;
}
