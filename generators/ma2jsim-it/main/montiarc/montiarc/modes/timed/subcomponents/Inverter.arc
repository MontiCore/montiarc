/* (c) https://github.com/MontiCore/monticore */
package montiarc.modes.timed.subcomponents;

import montiarc.types.OnOff;

component Inverter {
  port
   in OnOff i,
   out OnOff o;

  <<timed>> automaton {
    initial state S;
    S -> S i / {
      if (i == OnOff.ON) { o = OnOff.OFF; };
      if (i == OnOff.OFF) { o = OnOff.ON; };
    };
  }
}
