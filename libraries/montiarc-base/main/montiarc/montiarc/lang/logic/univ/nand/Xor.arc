/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang.logic.univ.nand;

import montiarc.lang.logic.gate.Nand;

component Xor {

  port <<sync>> in boolean a,
       <<sync>> in boolean b;
  port <<sync>> out boolean q;

  Nand nandAB;
  a -> nandAB.a;
  b -> nandAB.b;

  Nand nandA;
  a -> nandA.a;
  nandAB.q -> nandA.b;

  Nand nandB;
  b -> nandB.a;
  nandAB.q -> nandB.b;

  Nand nand;
  nandA.q -> nand.a;
  nandB.q -> nand.b;

  nand.q -> q;
}
