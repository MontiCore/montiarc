/* (c) https://github.com/MontiCore/monticore */
package compute;

import Types.OnOff;

/**
 * The component delays its received input by one clock cycle.
 */
component Delay {

  port <<sync>> in OnOff i;
  port <<sync, delayed>> out OnOff o;

  init {
    o = OnOff.OFF;
  }

  compute {
    o = i;
  }
}
