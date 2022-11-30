/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang.logic.circuit;

import montiarc.lang.logic.Delay;
import montiarc.lang.logic.gate.Nor;

component SRNorLatch {

  // set bit
  port <<sync>> in boolean s;

  // reset bit
  port <<sync>> in boolean r;

  // state bits
  port <<sync>> out boolean q0, q1;

  Nor norS, norR;

  s -> norS.a;
  r -> norR.a;

  norS.q -> q0;
  norR.q -> q1;

  Delay delayS, delayR;

  norS.q -> delayS.i;
  norR.q -> delayR.i;

  delayS.o -> norR.b;
  delayR.o -> norS.b;

}
