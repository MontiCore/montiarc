/* (c) https://github.com/MontiCore/monticore */
package montiarc.variability.sync.compute;

import montiarc.types.OnOff;

component NameOverlap {
  feature onOff;

  varif (onOff) {
    port sync out OnOff o;
    OnOff var = OnOff.OFF;
  } else {
    port sync out Integer o;
    Integer var = 0;
  }

  compute {
    o = var;
  }
}
