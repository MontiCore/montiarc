/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.compute;

import montiarc.types.OnOff;

component Medium2x2 {

  port in OnOff i1, i2;
  port out OnOff o1, o2;

  compute {
    o1 = i1;
    o2 = i2;
  }
}
