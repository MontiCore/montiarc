/* (c) https://github.com/MontiCore/monticore */
package compute;

import types.OnOff;

/**
 * The component delays its received input by one clock cycle.
 */
component Delay {

  port <<sync>> in OnOff i;
  port <<causalsync>> out OnOff o;

  init {
    o = OnOff.OFF;
  }

  compute {
    o = i;
  }
}
