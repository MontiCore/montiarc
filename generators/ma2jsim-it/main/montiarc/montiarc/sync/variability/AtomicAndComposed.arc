/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.variability;

import montiarc.types.OnOff;
import montiarc.sync.automata.Inverter;

component AtomicAndComposed {

  feature atomic;

  port in OnOff i;
  port out OnOff o;

  varif (atomic) {
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
    Inverter inverter;
    i -> inverter.i;
    inverter.o -> o;
  }
}
