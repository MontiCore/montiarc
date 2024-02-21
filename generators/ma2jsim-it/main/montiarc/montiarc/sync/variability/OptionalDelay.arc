/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.variability;

import montiarc.types.OnOff;

component OptionalDelay {
  feature delayed;

  port in OnOff i;
  varif (delayed) {
    port <<delayed>> out OnOff o;
    init { o = OnOff.OFF; }
  } else {
    port out OnOff o;
  }

  compute {
    o = i;
  }
}
