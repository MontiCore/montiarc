/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata;

import montiarc.types.OnOff;

component Send2Messages {
  port in OnOff p;
  port out OnOff o;

  <<timed>> automaton {
    initial state S;

    S -> S p / { o = p; o = p; };
  }
}
