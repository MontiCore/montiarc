/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.composition;

import montiarc.sync.automata.Medium;
import montiarc.types.OnOff;

component Encapsulation {

  port in OnOff i;
  port out OnOff o;

  Medium sub;

  i -> sub.i;
  sub.o -> o;
}
