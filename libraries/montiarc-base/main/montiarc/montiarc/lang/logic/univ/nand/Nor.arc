/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang.logic.univ.nand;

import montiarc.lang.logic.gate.Nand;

component Nor {

  port <<sync>> in boolean a,
       <<sync>> in boolean b;
  port <<sync>> out boolean q;

  Nand notA;
  a -> notA.a;
  a -> notA.b;

  Nand notB;
  b -> notB.a;
  b -> notB.b;

  Nand nand;
  notA.q -> nand.a;
  notB.q -> nand.b;

  Nand not;
  nand.q -> not.a;
  nand.q -> not.b;

  not.q -> q;
}
