/* (c) https://github.com/MontiCore/monticore */
package montiarc.statements;

import montiarc.types.OnOff;
import montiarc.types.OnOff.ON;
import montiarc.types.OnOff.OFF;

component SwitchConditional {

  port in int i;
  port in char c;
  port in String s;
  port in OnOff e;
  port out int o;

  automaton {
    initial state S;
    S -> S i / {
      switch (i) {
        case 1: o = 1; break;
        case 2: o = 2; break;
        default: o = -1; break;
      }
      switch (i) {
        default: o = -1; break;
        case 1: o = 1; break;
      }
    }
    S -> S c / {
      switch (c) {
        case 'a': o = 1; break;
        case 'b': o = 2; break;
        default: o = -1; break;
      }
    }
    /*
    S -> S s / {
      switch (s) {
        case "a": o = 1; break;
        case "b": o = 2; break;
        default: o = -1; break;
      }
    }*/
    S -> S e / {
      switch (e) {
        case OnOff.ON: o = 1; break;
        case OnOff.OFF: o = 2; break;
        default: o = -1; break;
      }
    }
    S -> S e / {
      switch (e) {
        case ON: o = 1; break;
        case OFF: o = 2; break;
        default: o = -1; break;
      }
    }
  }
}
