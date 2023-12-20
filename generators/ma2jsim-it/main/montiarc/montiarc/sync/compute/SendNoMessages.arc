/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.compute;

import montiarc.types.OnOff;

component SendNoMessages {
  port in OnOff p;
  port out OnOff o;

  compute { }
}
