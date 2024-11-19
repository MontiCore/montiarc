/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.compute;

import montiarc.types.OnOff;

component NameOverlap {
  feature onOff;

  varif (onOff) {
    port out OnOff o;
    OnOff var = OnOff.OFF;
  } else {
    port out Integer o;
    Integer var = 0;
  }

  compute {
    o = var;
  }
}
