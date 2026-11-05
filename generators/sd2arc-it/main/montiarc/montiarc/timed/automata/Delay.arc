/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata;

import montiarc.types.OnOff;

component Delay {

  port in OnOff i;
  port out OnOff o;

  <<delayed>> automaton {

    initial state S {
      entry / { o = OnOff.OFF; }
    }

    S -> S i / { o = i; }
  }
}
