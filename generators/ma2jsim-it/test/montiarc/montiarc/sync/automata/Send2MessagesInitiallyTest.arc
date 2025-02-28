/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

import montiarc.types.OnOff;
import montiarc.maunit.api.AssertEqualsUntimed;
import montiarc.maunit.api.Emit;
import java.util.List;

<<test, ticks=[1,1], input=[OnOff.OFF, OnOff.ON]>>
component Send2MessagesInitiallyTest(OnOff input) {
  Send2MessagesInitially sut();

  generator.out -> sut.p;
  sut.o -> assertions.actual;

  Emit<OnOff> generator(input);

  AssertEqualsUntimed<OnOff> assertions([OnOff.OFF, OnOff.OFF, input, input]);
}
