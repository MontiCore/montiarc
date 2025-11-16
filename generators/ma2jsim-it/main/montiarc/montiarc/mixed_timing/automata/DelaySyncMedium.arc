/* (c) https://github.com/MontiCore/monticore */
package montiarc.mixed_timing.automata;

import montiarc.types.OnOff;

component DelaySyncMedium {
  port sync in OnOff inSync;
  port in OnOff inEvent;

  port sync out OnOff oSync;
  port out OnOff oEvent;

  <<delayed>> automaton {
    initial state S1 {
      initial state S11 {
        entry / {
          oSync=OnOff.OFF;
        }
      }
      state S12;
    }

    S1 -> S12 / {
      oSync = inSync;
    }
    S1 -> S12 inEvent / {
      oEvent = inEvent;
    }
  }
}
