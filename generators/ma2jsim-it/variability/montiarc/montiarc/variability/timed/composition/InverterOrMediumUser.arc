/* (c) https://github.com/MontiCore/monticore */
package montiarc.variability.timed.composition;

import montiarc.timed.automata.Inverter;
import montiarc.timed.automata.Medium;
import montiarc.types.OnOff;

component InverterOrMediumUser {
  constraint(sub.inv == true);

  port in OnOff i;
  port out OnOff o;

  InverterOrMedium sub();

  i -> sub.i;
  sub.o -> o;
}
