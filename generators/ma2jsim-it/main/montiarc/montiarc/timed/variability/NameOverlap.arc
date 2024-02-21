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

    Medium sub;
    i -> sub.i;
    sub.o -> o;
  } else {
    port in Integer i;
    port out Integer o;

    GenericMedium<Integer> sub();
    i -> sub.i;
    sub.o -> o;
  }
}
