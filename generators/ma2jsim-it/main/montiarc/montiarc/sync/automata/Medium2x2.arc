/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

import montiarc.types.OnOff;

component Medium2x2 {

  port in OnOff i1, i2;
  port out OnOff o1, o2;

  <<sync>> automaton {
    initial state S;

    S -> S / {
      o1 = i1;
      o2 = i2;
    };
  }
}
