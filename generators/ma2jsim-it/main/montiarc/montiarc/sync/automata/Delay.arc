/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

import montiarc.types.OnOff;

component Delay {

  port sync in OnOff i;
  port <<delayed>> sync out OnOff o;

  automaton {
    initial { o = OnOff.OFF; } state S;

    S -> S / { o = i; };
  }
}
