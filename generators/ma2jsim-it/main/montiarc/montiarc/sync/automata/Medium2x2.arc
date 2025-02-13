/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

import montiarc.types.OnOff;

component Medium2x2 {

  port sync in OnOff i1, i2;
  port sync out OnOff o1, o2;

  automaton {
    initial state S;

    S -> S / {
      o1 = i1;
      o2 = i2;
    };
  }
}
