/* (c) https://github.com/MontiCore/monticore */
package montiarc.mixed_timing.automata;

import montiarc.types.OnOff;

component DelaySyncMedium {
  port sync in OnOff inSync;
  port in OnOff inEvent;

  port sync out OnOff oSync;
  port out OnOff oEvent;

  <<delayed>> automaton {
    initial { oSync=OnOff.OFF; } state S;

    S -> S / {
      oSync = inSync;
    };
    S -> S inEvent / {
      oEvent = inEvent;
    };
  }
}
