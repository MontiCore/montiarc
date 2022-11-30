/* (c) https://github.com/MontiCore/monticore */
package compute;

import types.OnOff;

/**
 * The component forwards its inputs without delay.
 */
component Forward {

  port <<sync>> in OnOff i;
  port <<sync>> out OnOff o;

  init {
    o = OnOff.OFF;
  }

  compute {
    o = i;
  }
}
