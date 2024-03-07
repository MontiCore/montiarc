/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.variability;

import montiarc.timed.automata.Inverter;
import montiarc.timed.automata.Medium;
import montiarc.types.OnOff;

component InverterOrMediumParameter(boolean pInverter = false) {
  port in OnOff i;
  port out OnOff o;

  varif(pInverter) {
    Inverter inverter;
    i -> inverter.i;
    inverter.o -> o;
  }

  varif(!pInverter) {
    Medium medium;
    i -> medium.i;
    medium.o -> o;
  }
}
