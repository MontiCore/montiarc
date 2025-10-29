/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.variability;

import montiarc.types.OnOff;
import montiarc.sync.automata.Inverter;

component AtomicAndComposed {

  feature atomic;

  port sync in OnOff i;
  port sync out OnOff o;

  varif (atomic) {
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
    Inverter inverter;
    i -> inverter.i;
    inverter.o -> o;
  }
}
