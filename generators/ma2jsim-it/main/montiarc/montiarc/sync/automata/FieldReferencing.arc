/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

import montiarc.types.OnOff;

component FieldReferencing {
  int x = 1;
  int y = x + 1;
  int z = x + y;

  port sync out int o;

  automaton {
    initial state S;

    S -> S / {
      o = z;
    };
  }
}
