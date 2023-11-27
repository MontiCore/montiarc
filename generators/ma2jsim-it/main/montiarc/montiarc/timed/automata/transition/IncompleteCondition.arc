/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata.transition;

import montiarc.types.OnOff;

// Test case: a message event only conditionally leads to the execution of a transition.
component IncompleteCondition {
  port in int i;
  port out OnOff o;

  <<timed>> automaton {
    initial state S;

    S -> S [i > 0] i / { o = OnOff.ON; };
  }
}
