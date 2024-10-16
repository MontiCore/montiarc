/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.composition;

import montiarc.timed.automata.Medium;
import montiarc.types.OnOff;

component EncapsulationSyncIn {

  port <<sync>> in OnOff i;
  port out OnOff o;

  Medium sub;

  i -> sub.i;
  sub.o -> o;
}
