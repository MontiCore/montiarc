/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

import montiarc.types.OnOff;

component Send2MessagesInitially {
  port in OnOff p;
  port <<delayed>> out OnOff o;

  <<sync>> automaton {
    initial {
      o = OnOff.OFF;
      o = OnOff.OFF;
    } state S;

    S -> S / { o = p; };
  }
}
