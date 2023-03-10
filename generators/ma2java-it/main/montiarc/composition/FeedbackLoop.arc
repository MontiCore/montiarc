/* (c) https://github.com/MontiCore/monticore */
package composition;

import Types.OnOff;

/**
 * Composed component with an input and output port. Its behavior is defined
 * through its subcomponents. The component's topology of subcomponents features
 * a feedback loop through which the component quasi implements the same
 * architecture as SequentialComposition (if flattened).
 */
component FeedbackLoop {

  port <<sync>> in OnOff i;
  port <<sync>> out OnOff o;

  /**
   * The component's topology of subcomponents features a feedback loop
   */
  ParallelComposition sub;

  // Incoming channel
  i -> sub.i1;

  // Feedback loop
  sub.o1 -> sub.i2;

  // Outgoing channel
  sub.o2 -> o;
}
