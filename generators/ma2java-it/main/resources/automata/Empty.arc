/* (c) https://github.com/MontiCore/monticore */
package automata;

import types.OnOff;

/**
 * Empty atomic component, the component should not produce any output.
 */
component Empty {
  port <<sync>> in OnOff i;
  port <<sync>> out OnOff o;

}
