/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

import montiarc.types.OnOff;

component Delay {

  port in OnOff i;
  port <<delayed>> out OnOff o;

  <<sync>> automaton {
    initial { o = OnOff.OFF; } state S;

    S -> S / { o = i; };
  }
}
