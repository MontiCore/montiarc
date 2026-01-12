/* (c) https://github.com/MontiCore/monticore */
package montiarc.core;

import montiarc.types.OnOff;
import montiarc.types.OnOff;
import montiarc.types.OnOff;
import montiarc.tYpEs.OnOff;
import mOnTiArc.tYpEs.OnOff;

component MultiImport {

  OnOff p = OnOff.ON;

  port in OnOff i;
  port out OnOff o;

  automaton {
    initial state S;

    S -> S i / {
      o = OnOff.OFF;
    }
  }
}
