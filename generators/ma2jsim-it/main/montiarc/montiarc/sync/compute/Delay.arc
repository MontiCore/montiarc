/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.compute;

import montiarc.types.OnOff;

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
