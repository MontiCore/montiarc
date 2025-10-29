/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

import montiarc.types.OnOff;

component Source {

  port sync out OnOff o;

  automaton {
    initial state S;

    S -> S / { o = OnOff.ON; }
  }
}
