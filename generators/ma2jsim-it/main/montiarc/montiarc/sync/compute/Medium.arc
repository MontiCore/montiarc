/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.compute;

import montiarc.types.OnOff;

component Medium {

  port sync in OnOff i;
  port sync out OnOff o;

  compute {
    o = i;
  }
}
