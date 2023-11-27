/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata.transition;

import montiarc.types.OnOff;

// Test case: a message event only conditionally leads to the execution of a transition.
component IncompleteCondition {
  port in int i;
  port out OnOff o;

  <<sync>> automaton {
    initial state S;

    S -> S [i > 0] / { o = OnOff.ON; };
  }
}
