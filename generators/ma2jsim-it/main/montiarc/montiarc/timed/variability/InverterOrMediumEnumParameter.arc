/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.variability;

import montiarc.timed.automata.Inverter;
import montiarc.timed.automata.Medium;
import montiarc.types.OnOff;

component InverterOrMediumEnumParameter(OnOff pInverter = OnOff.OFF) {
  port in OnOff i;
  port out OnOff o;

  varif(pInverter == OnOff.ON) {
    Inverter inverter;
    i -> inverter.i;
    inverter.o -> o;
  }

  varif(pInverter == OnOff.OFF) {
    Medium medium;
    i -> medium.i;
    medium.o -> o;
  }
}
