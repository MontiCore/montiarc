/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

import montiarc.types.OnOff;

component Medium {

  port sync in OnOff i;
  port sync out OnOff o;

  automaton {
    initial state S;

    S -> S / {
      o = i;
    };
  }
}
