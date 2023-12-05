/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.compute;

import montiarc.types.OnOff;

component Switch {

  port in OnOff i1;
  port in OnOff i2;
  port out OnOff o;

  compute {
    if (i1 == OnOff.ON) {
      o = i2;
    } else {
      o = OnOff.OFF;
    }
  }
}
