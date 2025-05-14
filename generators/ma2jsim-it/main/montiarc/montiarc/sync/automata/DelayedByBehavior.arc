/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

import montiarc.types.OnOff;

component DelayedByBehavior {

  port sync in OnOff i;
  port sync out OnOff o;

  <<delayed>> automaton {
    initial { o = OnOff.OFF; } state S;

    S -> S / { o = i; };
  }
}
