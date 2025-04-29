/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.composition;

import montiarc.sync.automata.Medium2x2;
import montiarc.types.OnOff;

component FeedbackLoop {

  port sync in OnOff i;
  port sync out OnOff o;

  Medium2x2 medium;

  i -> medium.i1;

  medium.o1 -> delay.i;

  montiarc.sync.automata.Delay delay;

  delay.o -> medium.i2;

  medium.o2 -> o;

}
