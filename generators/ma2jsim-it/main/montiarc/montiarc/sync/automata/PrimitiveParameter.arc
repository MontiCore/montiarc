/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

import montiarc.types.OnOff;

<<deploy>> component PrimitiveParameter(int p) {

  port sync out int o;

  automaton {
    initial state S;

    S -> S / { o = p; }
  }
}
