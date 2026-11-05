/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

import montiarc.types.OnOff;

<<deploy>> component Parameter(OnOff p) {

  port sync out OnOff o;

  automaton {
    initial state S;

    S -> S / { o = p; }
  }
}
