/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata.transition;

import montiarc.types.OnOff;

// There exists an input port which is not used as a trigger for behavior and its messages should be dropped.
component IgnoresInPort {
  port in OnOff i1;
  port in OnOff i2;
  port out OnOff o;

  <<timed>> automaton {
    initial state S;
    S -> S i1 / { o = i1; };
  }
}
