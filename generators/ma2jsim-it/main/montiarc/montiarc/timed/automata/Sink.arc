/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata;

import montiarc.types.OnOff;

component Sink {

  port in OnOff i;

  <<timed>> automaton {
    initial state Init;
    state On;
    state Off;

    Init -> On [i == OnOff.ON] i;
    Init -> Off [i == OnOff.OFF] i;
    On -> On [i == OnOff.ON] i;
    On -> Off [i == OnOff.OFF] i;
    Off -> On [i == OnOff.ON] i;
    Off -> Off [i == OnOff.OFF] i;
  }
}
