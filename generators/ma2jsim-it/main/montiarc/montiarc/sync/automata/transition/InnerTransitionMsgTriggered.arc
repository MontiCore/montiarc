/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata.transition;

import montiarc.types.OnOff;

component InnerTransitionMsgTriggered {
  port sync in int i;
  port sync out OnOff o;

  automaton {
    initial state S {
      -> / { o = OnOff.OFF; }
    }
  }
}
