/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.variability;

import montiarc.types.OnOff;

component OptionalDelay {
  feature delayed;

  port sync in OnOff i;
  varif (delayed) {
    port <<delayed>> sync out OnOff o;
    init { o = OnOff.OFF; }
  } else {
    port sync out OnOff o;
  }

  compute {
    o = i;
  }
}
