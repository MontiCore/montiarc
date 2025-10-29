/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata;

import montiarc.types.OnOff;

component DelayedCombiner {
  port in OnOff i1;
  port in OnOff i2;
  port out OnOff o;

  <<delayed>> automaton {
    initial state S;
    S -> S i1 / { o = i1; }
    S -> S i2 / { o = i2; }
  }
}
