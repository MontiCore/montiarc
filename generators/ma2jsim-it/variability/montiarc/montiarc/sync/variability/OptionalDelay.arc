/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.variability;

import montiarc.types.OnOff;

component OptionalDelay {
  feature delayed;

  port sync in OnOff i;
  varif (delayed) {
    port sync out OnOff o;
    init { o = OnOff.OFF; }
    <<delayed>> compute {
      o = i;
    }
  } else {
    port sync out OnOff o;
    compute {
      o = i;
    }
  }
}
