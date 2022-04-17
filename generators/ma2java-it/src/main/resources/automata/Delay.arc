/* (c) https://github.com/MontiCore/monticore */
package automata;

import types.OnOff;

/**
 * Atomic component with an input and output port. Its behavior is defined
 * through an automaton. The automaton delays messages received via its input
 * channel by one computation cycle.
 */
component Delay {

  port in OnOff i;
  port out OnOff o;

  /**
   * The automaton delays and then forwards messages.
   */
  automaton {
    // initial state to delay initial output
    initial state Start;
    // a state for each message kind
    state On;
    state Off;

    // transition according to the input received
    Start -> On [ i == OnOff.ON ];
    Start -> Off [ i == OnOff.OFF ];

    /* send message according to the current state,
     transition according to the input received */
    On -> On [ i == OnOff.ON ] / { o = OnOff.ON; };
    On -> Off [ i == OnOff.OFF ] / { o = OnOff.ON; };
    Off -> On [ i == OnOff.ON ] / { o = OnOff.OFF; };
    Off -> Off [ i == OnOff.OFF ] / { o = OnOff.OFF; };
  }
}
