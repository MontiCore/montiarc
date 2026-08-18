/* (c) https://github.com/MontiCore/monticore */
package montiarc.oracle;

import montiarc.types.OnOff;
import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitTimed;

<<test, ticks=4, input=[<OnOff.ON, Tick, OnOff.ON, Tick, OnOff.ON, Tick, OnOff.ON, Tick>], expected=[<1, Tick, 1, Tick, 1, Tick, 1, Tick>]>>
component CompositionLeastUsedStereotypeTest(EventStream<OnOff> input, EventStream<int> expected) {
  CompositionLeastUsedStereotype sut;

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitTimed<OnOff> generator(input);

  AssertEqualsTimed<int> assertions(expected);
}
