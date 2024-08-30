/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang.logic.circuit;

import montiarc.lang.TSDelay;
import montiarc.lang.logic.gate.Nand;

component SRNandLatch {

  // set bit
  port <<sync>> in boolean s;

  // reset bit
  port <<sync>> in boolean r;

  // state bits
  port <<sync>> out boolean q0, q1;

  Nand nandS, nandR;

  s -> nandS.a;
  r -> nandR.a;

  nandS.q -> q0;
  nandR.q -> q1;

  TSDelay<Boolean> delayS(false), delayR(false);

  nandS.q -> delayS.i;
  nandR.q -> delayR.i;

  delayS.o -> nandR.b;
  delayR.o -> nandS.b;

}
