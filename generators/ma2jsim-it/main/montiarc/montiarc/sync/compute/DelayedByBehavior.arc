/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.compute;

import montiarc.types.OnOff;

component DelayedByBehavior {

  port sync in OnOff i;
  port sync out OnOff o;

  init {
    o = OnOff.OFF;
  }

  <<delayed>> compute {
    o = i;
  }
}
