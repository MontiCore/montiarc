/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.variability;

import montiarc.timed.automata.Inverter;
import montiarc.timed.automata.Medium;
import montiarc.types.OnOff;

component InverterOrMedium {
  feature inv, med;
  constraint(inv != med);

  port in OnOff i;
  port out OnOff o;

  varif(inv) {
    Inverter inverter;
    i -> inverter.i;
    inverter.o -> o;
  }

  varif(med) {
    Medium medium;
    i -> medium.i;
    medium.o -> o;
  }
}
