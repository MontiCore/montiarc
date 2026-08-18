/* (c) https://github.com/MontiCore/monticore */
package montiarc.oracle;

import montiarc.types.OnOff;
import montiarc.maunit.api.AssertEqualsUntimed;
import montiarc.maunit.api.EmitTimed;

<<test, ticks=4, input=[<OnOff.ON, Tick, OnOff.ON, Tick, OnOff.ON, Tick, OnOff.ON, Tick>], expected=[Untimed<1, 2, 1, 2>], oracle="leastUsed">>
component LeastUsedStereotypeTest(EventStream<OnOff> input, UntimedStream<int> expected) {
  LeastUsedStereotype sut;

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitTimed<OnOff> generator(input);

  AssertEqualsUntimed<int> assertions(expected);
}
