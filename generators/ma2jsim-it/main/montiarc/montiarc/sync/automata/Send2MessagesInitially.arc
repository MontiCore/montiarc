/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

import montiarc.types.OnOff;

component Send2MessagesInitially {
  port sync in OnOff p;
  port sync out OnOff o;

  <<delayed>> automaton {
    initial {
      o = OnOff.OFF;
      o = OnOff.OFF;
    } state S;

    S -> S / { o = p; }
  }
}
