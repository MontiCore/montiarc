/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

import montiarc.types.OnOff;

component Delay {

  port sync in OnOff i;
  port sync out OnOff o;

  <<delayed>> automaton {
    initial state Init {
      entry / { o = OnOff.OFF; }
    }

    Init -> S / { o = i; }

    state S;

    S -> S / { o = i; }
  }
}
