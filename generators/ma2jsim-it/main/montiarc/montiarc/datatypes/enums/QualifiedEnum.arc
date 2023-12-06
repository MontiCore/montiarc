/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.enums;

import montiarc.types.OnOff;

component QualifiedEnum {
  port out OnOff off;

  <<sync>> automaton {
    initial state S;
    S -> S / {
      off = OnOff.OFF;
    };
  }
}
