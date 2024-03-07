/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.variability;

import montiarc.timed.automata.Medium;
import montiarc.timed.automata.GenericMedium;
import montiarc.types.OnOff;

component NameOverlap {
  feature onOff;

  varif (onOff) {
    port in OnOff i;
    port out OnOff o;
    OnOff var = OnOff.OFF;

    Medium sub;
    i -> sub.i;
    sub.o -> o;
  } else {
    port in Integer i;
    port out Integer o;
    Integer var = 0;

    GenericMedium<Integer> sub();
    i -> sub.i;
    sub.o -> o;
  }
}
