/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.compute;

import montiarc.types.OnOff;

component Inverter {

  port in OnOff i;
  port out OnOff o;

  compute {
    if (i == OnOff.ON) {
      o = OnOff.OFF;
    } else {
      o = OnOff.ON;
    }
  }
}
