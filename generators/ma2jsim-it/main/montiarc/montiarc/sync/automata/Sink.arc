/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

import montiarc.types.OnOff;

component Sink {

  port in OnOff i;

  <<sync>> automaton {
    initial state Init;
    state On;
    state Off;

    Init -> On [i == OnOff.ON];
    Init -> Off [i == OnOff.OFF];
    On -> On [i == OnOff.ON];
    On -> Off [i == OnOff.OFF];
    Off -> On [i == OnOff.ON];
    Off -> Off [i == OnOff.OFF];
  }
}
