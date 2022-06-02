/* (c) https://github.com/MontiCore/monticore */
package automata;

import types.Direction;
import types.OnOff;

/**
 * Atomic component with a multiple output ports. Its behavior is defined
 * through an automaton. The automaton should send initial messages over its
 * output channels.
 */
component InitialActionMultiplePorts {

  port out OnOff o1;
  port out Direction o2, o3;

  automaton {
    // initial action, emit OFF, LEFT, BACKWARDS
    initial {
      o1 = OnOff.OFF;
      o2 = Direction.LEFT;
      o3 = Direction.BACKWARDS;
    } state A;
    state B;

    // transition to B, emit ON, RIGHT, FORWARDS
    A -> B / {
      o1 = OnOff.ON;
      o2 = Direction.RIGHT;
      o3 = Direction.FORWARDS;
    };

    // transition to A, emit OFF, LEFT, BACKWARDS
    B -> A / {
      o1 = OnOff.OFF;
      o2 = Direction.LEFT;
      o3 = Direction.BACKWARDS;
    };
  }
}
