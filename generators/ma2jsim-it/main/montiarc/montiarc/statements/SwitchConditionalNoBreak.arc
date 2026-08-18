/* (c) https://github.com/MontiCore/monticore */
package montiarc.statements;

import montiarc.types.OnOff;
import montiarc.types.OnOff.ON;
import montiarc.types.OnOff.OFF;

component SwitchConditionalNoBreak {

  port in int i;
  port in char c;
  port in String s;
  port in OnOff e;
  port out int o;

  automaton {
    initial state S;
    S -> S i / {
      int one = 1;
      int two = one + 1;
      switch (i) {
        case one: o = 1;
        case two: o = 2;
        default: o = -1;
      }
      switch (i) {
        default: o = -1;
        case 1: o = 1;
      }
    }
    S -> S c / {
      switch (c) {
        case 'a': o = 1;
        case 'b': o = 2;
        default: o = -1;
      }
    }
    S -> S s / {
      switch (s) {
        case "a": o = 1;
        case "b": o = 2;
        default: o = -1;
      }
    }
    S -> S e / {
      switch (e) {
        case OnOff.ON: o = 1;
        case OnOff.OFF: o = 2;
        default: o = -1;
      }
    }
    S -> S e / {
      switch (e) {
        case ON: o = 1;
        case OFF: o = 2;
        default: o = -1;
      }
    }
  }
}
