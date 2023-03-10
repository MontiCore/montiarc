/* (c) https://github.com/MontiCore/monticore */
package automata;

import Types.OnOff;

/**
 * The component delays its received input by one clock cycle.
 */
component Delay {

  port <<sync>> in OnOff i;
  port <<sync, delayed>> out OnOff o;

  automaton {
    initial state S;

    // emit received messages, the port delays
    S -> S / { o = i; };
  }
}
