/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang.logic.univ.nand;

import montiarc.lang.logic.gate.Nand;

component And {

  port <<sync>> in boolean a,
       <<sync>> in boolean b;
  port <<sync>> out boolean q;

  Nand nand;
  a -> nand.a;
  b -> nand.b;

  Nand not;
  nand.q -> not.a;
  nand.q -> not.b;

  not.q -> q;
}
