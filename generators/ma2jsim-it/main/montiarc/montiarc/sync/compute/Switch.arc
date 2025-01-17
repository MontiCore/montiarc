/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.compute;

import montiarc.types.OnOff;

component Switch {

  port <<sync>> in OnOff i1;
  port <<sync>> in OnOff i2;
  port out OnOff o;

  compute {
    if (i1 == OnOff.ON) {
      o = i2;
    } else {
      o = OnOff.OFF;
    }
  }
}
