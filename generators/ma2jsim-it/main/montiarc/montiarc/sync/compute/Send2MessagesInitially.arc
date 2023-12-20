/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.compute;

import montiarc.types.OnOff;

component Send2MessagesInitially {
  port in OnOff p;
  port <<delayed>> out OnOff o;

  init {
    o = OnOff.OFF;
    o = OnOff.OFF;
  }

  compute { o = p; }
}
