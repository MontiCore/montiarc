/* (c) https://github.com/MontiCore/monticore */
package montiarc.variability.timed.composition;

import montiarc.timed.automata.Inverter;
import montiarc.timed.automata.Medium;
import montiarc.types.OnOff;

component SingleVariant {
  feature f;

  varif(f) {
    port in OnOff i;
    port out OnOff o;

    Medium medium;
    i -> medium.i;
    medium.o -> o;
  }

  constraint(f);
}
