/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

import montiarc.types.OnOff;

component Switch {

  port in OnOff i1;
  port in OnOff i2;
  port out OnOff o;

  <<sync>> automaton {
    initial state S;

    S -> S [i1 == OnOff.ON] / { o = i2; };
    S -> S [i1 == OnOff.OFF] / { o = OnOff.OFF; };
  }
}
