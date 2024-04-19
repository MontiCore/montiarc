/* (c) https://github.com/MontiCore/monticore */
package montiarc.modes.sync.subcomponents;

import montiarc.types.OnOff;
component Forwarder {
  port
   in OnOff i,
   out OnOff o;

  <<sync>> compute {
    o = i;
  }
}
