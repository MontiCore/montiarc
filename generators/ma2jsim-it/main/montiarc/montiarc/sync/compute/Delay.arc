/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.compute;

import montiarc.types.OnOff;

component Delay {

  port in OnOff i;
  port <<delayed>> out OnOff o;

  init {
    o = OnOff.OFF;
  }

  compute {
    o = i;
  }
}
