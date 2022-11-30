/* (c) https://github.com/MontiCore/monticore */
package composition;

import types.OnOff;
import automata.Delay;

/**
 * Composed component with an input and output port. Its behavior is defined
 * through its subcomponents. The component encapsulates a single subcomponent,
 * consequently the component's behavior should match its subcomponent's
 * behavior.
 */
component Encapsulation {

  port <<sync>> in OnOff i;
  port <<sync>> out OnOff o;

  Delay subComp;

  /**
   * The component forwards messages from and to its subcomponent.
   */
  i -> subComp.i;
  subComp.o -> o;
}
