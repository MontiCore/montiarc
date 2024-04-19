/* (c) https://github.com/MontiCore/monticore */
package montiarc.modes.sync.subcomponents;

import montiarc.types.OnOff;

component Inverter {
  port
   in OnOff i,
   out OnOff o;

  <<sync>> compute {
    if (i == OnOff.ON) { o = OnOff.OFF; }
    if (i == OnOff.OFF) { o = OnOff.ON; }
  }
}
