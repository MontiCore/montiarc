/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata.transition;

import montiarc.types.OnOff;

// Non-triggered transitions in timed automatons should act as tick-triggered
component NoTrigger {
  port in int i;
  port out OnOff o;

  <<timed>> automaton {
    initial state S;
    S -> S / { o = OnOff.ON; };
  }
}
