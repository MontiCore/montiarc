/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang.logic.univ.nand;

import montiarc.lang.logic.gate.Nand;

component Not {

  port <<sync>> in boolean a;
  port <<sync>> out boolean q;

  Nand nand;
  a -> nand.a;
  a -> nand.b;

  nand.q -> q;
}
