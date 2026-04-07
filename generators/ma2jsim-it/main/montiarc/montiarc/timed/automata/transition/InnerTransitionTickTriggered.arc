/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata.transition;

import montiarc.types.OnOff;

component InnerTransitionTickTriggered {
  port in int i;
  port out OnOff o;

  automaton {
    initial state S {
      -> i / { o = OnOff.OFF; }
    }
  }
}
