/* (c) https://github.com/MontiCore/monticore */
package montiarc.variability.atomic;

import montiarc.types.OnOff;

component InverterSelectableTiming {

  feature sync;

  port in OnOff i;
  port out OnOff o;

  varif (sync) {
    <<sync>> automaton {
        initial state S;

        S -> S [i == OnOff.ON] / {
          o = OnOff.OFF;
        };

        S -> S [i == OnOff.OFF] / {
          o = OnOff.ON;
        };
      }
  } else {
    <<timed>> automaton {
      initial state S;

      S -> S [i == OnOff.ON] i / {
        o = OnOff.OFF;
      };

      S -> S [i == OnOff.OFF] i / {
        o = OnOff.ON;
      };
    }
  }
}
