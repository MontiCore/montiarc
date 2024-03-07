/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

import montiarc.types.OnOff;

component PrimitiveParameter(int p) {

  port out int o;

  <<sync>> automaton {
    initial state S;

    S -> S / { o = p; };
  }
}
