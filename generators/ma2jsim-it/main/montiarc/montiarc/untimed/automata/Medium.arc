/* (c) https://github.com/MontiCore/monticore */
package montiarc.untimed.automata;

import montiarc.types.OnOff;

component Medium {

  port in OnOff i;
  port out OnOff o;

  automaton {
    initial state S;

    S -> S i / {
      o = i;
    };
  }
}
