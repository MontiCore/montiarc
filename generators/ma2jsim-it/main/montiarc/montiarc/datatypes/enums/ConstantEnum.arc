/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.enums;

import montiarc.types.OnOff.*;

component ConstantEnum {
  port out OnOff off;

  <<sync>> automaton {
    initial state S;
    S -> S / {
      off = OFF;
    };
  }
}
