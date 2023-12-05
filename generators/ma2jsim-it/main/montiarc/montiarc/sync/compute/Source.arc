/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.compute;

import montiarc.types.OnOff;

component Source {

  port out OnOff o;

  compute {
    o = OnOff.ON;
  }
}
