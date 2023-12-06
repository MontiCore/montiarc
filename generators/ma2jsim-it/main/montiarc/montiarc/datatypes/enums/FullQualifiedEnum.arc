/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.enums;

import montiarc.types.OnOff;

component FullQualifiedEnum {
  port out OnOff off;

  <<sync>> automaton {
    initial state S;
    S -> S / {
      off = montiarc.types.OnOff.OFF;
    };
  }
}
