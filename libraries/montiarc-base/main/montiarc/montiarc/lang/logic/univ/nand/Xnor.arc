/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang.logic.univ.nand;

import montiarc.lang.logic.gate.Nand;

component Xnor {

  port <<sync>> in boolean a,
       <<sync>> in boolean b;
  port <<sync>> out boolean q;

  Nand notA;
  a -> notA.a;
  a -> notA.b;

  Nand notB;
  b -> notB.a;
  b -> notB.b;

  Nand nand1;
  a -> nand1.a;
  b -> nand1.b;

  Nand nand2;
  notA.q -> nand2.a;
  notB.q -> nand2.b;

  Nand nand3;
  nand1.q -> nand3.a;
  nand2.q -> nand3.b;

  nand3.q -> q;
}
