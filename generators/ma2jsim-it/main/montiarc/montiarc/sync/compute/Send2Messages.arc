/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.compute;

import montiarc.types.OnOff;

component Send2Messages {
  port sync in OnOff p;
  port sync out OnOff o;

  compute {
    o = p;
    o = p;
  }
}
