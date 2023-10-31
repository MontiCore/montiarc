/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.composition;

import montiarc.sync.automata.Delay;
import montiarc.sync.automata.Medium2x2;
import montiarc.types.OnOff;

component FeedbackLoop {

  port in OnOff i;
  port out OnOff o;

  Medium2x2 medium;

  i -> medium.i1;

  medium.o1 -> delay.i;

  Delay delay;

  delay.o -> medium.i2;

  medium.o2 -> o;

}
