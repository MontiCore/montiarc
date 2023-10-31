/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata;

import montiarc.types.OnOff;

component Source {

  port out OnOff o;

  <<timed>> automaton {
    initial state S;

    S -> S / { o = OnOff.ON; };
  }
}
