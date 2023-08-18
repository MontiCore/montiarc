/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

import montiarc.types.OnOff;

component Source {
  port out OnOff o;

  <<sync>> automaton {
    initial state S;

    S -> S / { o = OnOff.ON; };
  }
}
