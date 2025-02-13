/* (c) https://github.com/MontiCore/monticore */
package compute;

import Types.OnOff;

/**
 * The component delays its received input by one clock cycle.
 */
component Delay {

  port sync in OnOff i;
  port <<delayed>> sync out OnOff o;

  init {
    o = OnOff.OFF;
  }

  compute {
    o = i;
  }
}
