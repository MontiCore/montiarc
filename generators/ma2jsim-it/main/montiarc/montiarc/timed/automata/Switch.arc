/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata;

import montiarc.types.OnOff;

component Switch {

  port in OnOff i1;
  port in OnOff i2;
  port out OnOff o;

  <<timed>> automaton {
    initial state Off;
    state On;

    Off -> Off i2 / { o = OnOff.OFF; };
    On -> On i2 / { o = i2; };

    Off -> On [i1 == OnOff.ON] i1;
    Off -> Off [i1 == OnOff.OFF] i1;
    On -> On [i1 == OnOff.ON] i1;
    On -> Off [i1 == OnOff.OFF] i1;
  }
}
