/* (c) https://github.com/MontiCore/monticore */
package composition;

import types.OnOff;
import automata.Delay;
import automata.Inverter;

/**
 * Composed component with two input and output ports. Its behavior is defined
 * through its parallel composed subcomponents.
 */
component ParallelComposition {

  port <<sync>> in OnOff i1, i2;
  port <<sync>> out OnOff o1, o2;

  /**
   * The component's subcomponents are composed in parallel.
   */
  Delay c1;
  Inverter c2;

  // Incoming channels
  i1 -> c1.i;
  i2 -> c2.i;

  // Outgoing channels
  c1.o -> o1;
  c2.o -> o2;
}
