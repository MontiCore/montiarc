/* (c) https://github.com/MontiCore/monticore */
package composition;

import types.OnOff;
import automata.Delay;
import automata.Inverter;

/**
 * Composed component with an input and output port. Its behavior is defined
 * through its sequentially composed subcomponents.
 */
component SequentialComposition {

  port in OnOff i;
  port out OnOff o;

  /**
   * The component's subcomponents are sequentially composed.
   */
  Delay c1;
  Inverter c2;

  // Incoming channel
  i -> c1.i;

  // Hidden channel
  c1.o -> c2.i;

  // Outgoing channel
  c2.o -> o;
}
