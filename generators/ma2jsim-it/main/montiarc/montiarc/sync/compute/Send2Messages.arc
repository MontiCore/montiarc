/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.compute;

import montiarc.types.OnOff;

component Send2Messages {
  port in OnOff p;
  port out OnOff o;

  compute {
    o = p;
    o = p;
  }
}
