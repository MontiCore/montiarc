/* (c) https://github.com/MontiCore/monticore */
package montiarc.modes.sync.subcomponents;

import montiarc.types.OnOff;
component Forwarder {
  port
   sync in OnOff i,
   sync out OnOff o;

  compute {
    o = i;
  }
}
