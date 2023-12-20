/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata;

import montiarc.types.OnOff;

component Send2MessagesInitially {
  port in OnOff p;
  port <<delayed>> out OnOff o;

  <<timed>> automaton {
    initial {
      o = OnOff.OFF;
      o = OnOff.OFF;
    } state S;

    S -> S p / { o = p; };
  }
}
