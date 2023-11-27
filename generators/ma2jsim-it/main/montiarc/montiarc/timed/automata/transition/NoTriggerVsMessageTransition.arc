/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata.transition;

import montiarc.types.OnOff;

// Checks that transitions without triggers are triggered by ticks,
// but not executed as alternative for message-triggered transitions
// whose condition is invalid (like it was once implemented).
component NoTriggerVsMessageTransition {
  port in int i;
  port out OnOff o;

  <<timed>> automaton {
    initial state S;
    S -> S [i > 0] i / { o = OnOff.ON; };
    S -> S / { o = OnOff.OFF; };
  }
}
