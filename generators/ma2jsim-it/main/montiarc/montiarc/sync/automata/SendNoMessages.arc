/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

import montiarc.types.OnOff;

component SendNoMessages {
  port sync in OnOff p;
  port sync out OnOff o;

  automaton {
    initial state S;

    S -> S;
  }
}
