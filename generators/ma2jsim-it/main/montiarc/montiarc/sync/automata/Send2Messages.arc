/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

import montiarc.types.OnOff;

component Send2Messages {
  port in OnOff p;
  port out OnOff o;

  <<sync>> automaton {
    initial state S;

    S -> S / { o = p; o = p; };
  }
}
