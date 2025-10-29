/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

import montiarc.types.OnOff;

component Switch {

  port sync in OnOff i1;
  port sync in OnOff i2;
  port sync out OnOff o;

  automaton {
    initial state S;

    S -> S [i1 == OnOff.ON] / { o = i2; }
    S -> S [i1 == OnOff.OFF] / { o = OnOff.OFF; }
  }
}
