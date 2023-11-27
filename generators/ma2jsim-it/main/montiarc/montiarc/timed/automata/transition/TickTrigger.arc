/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata.transition;

import montiarc.types.OnOff;

component TickTrigger {
  port in int i;
  port out OnOff o;

  <<timed>> automaton {
    initial state S;
    S -> S i / { o = OnOff.OFF; };
    S -> S Tick / { o = OnOff.ON; };
  }
}
