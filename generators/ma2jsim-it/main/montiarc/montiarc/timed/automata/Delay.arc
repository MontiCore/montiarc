/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata;

import montiarc.types.OnOff;

component Delay {

  port in OnOff i;
  port out OnOff o;

  <<delayed>> automaton {
    initial { o = OnOff.OFF; } state S;

    S -> S i / { o = i; }
  }
}
