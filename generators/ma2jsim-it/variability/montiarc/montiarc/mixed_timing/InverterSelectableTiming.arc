/* (c) https://github.com/MontiCore/monticore */
package montiarc.mixed_timing;

import montiarc.types.OnOff;

component InverterSelectableTiming {

  feature sync;

  port sync in OnOff i;
  port out OnOff o;

  varif (sync) {
    automaton {
        initial state S;

        S -> S [i == OnOff.ON] / {
          o = OnOff.OFF;
        };

        S -> S [i == OnOff.OFF] / {
          o = OnOff.ON;
        };
      }
  } else {
    automaton {
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
