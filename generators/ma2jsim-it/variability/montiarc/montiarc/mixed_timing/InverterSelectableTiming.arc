/* (c) https://github.com/MontiCore/monticore */
package montiarc.mixed_timing;

import montiarc.types.OnOff;

component InverterSelectableTiming {

  feature sync;

  port out OnOff o;

  varif (sync) {
    port sync in OnOff i;
    automaton {
        initial state S;

        S -> S [i == OnOff.ON] / {
          o = OnOff.OFF;
        }

        S -> S [i == OnOff.OFF] / {
          o = OnOff.ON;
        }
      }
  } else {
    port in OnOff i;
    automaton {
      initial state S;

      S -> S [i == OnOff.ON] i / {
        o = OnOff.OFF;
      }

      S -> S [i == OnOff.OFF] i / {
        o = OnOff.ON;
      }
    }
  }
}
