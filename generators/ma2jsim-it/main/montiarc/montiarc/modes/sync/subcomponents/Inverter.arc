/* (c) https://github.com/MontiCore/monticore */
package montiarc.modes.sync.subcomponents;

import montiarc.types.OnOff;

component Inverter {
  port
   sync in OnOff i,
   sync out OnOff o;

  compute {
    if (i == OnOff.ON) { o = OnOff.OFF; }
    if (i == OnOff.OFF) { o = OnOff.ON; }
  }
}
