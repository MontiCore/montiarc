/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata;

import montiarc.types.OnOff;
import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitTimed;

<<test, ticks=3, input=[
  <OnOff.OFF, Tick, OnOff.OFF, Tick, OnOff.OFF>,
  <OnOff.OFF, Tick, OnOff.OFF, Tick, OnOff.ON>,
  <OnOff.OFF, Tick, OnOff.ON,  Tick, OnOff.ON>,
  <OnOff.ON,  Tick, OnOff.OFF, Tick, OnOff.ON>,
  <OnOff.ON,  Tick, OnOff.ON,  Tick, OnOff.OFF>,
  <OnOff.ON,  Tick, OnOff.ON,  Tick, OnOff.ON>
], expected=[
  <Tick, OnOff.OFF, Tick, OnOff.OFF, Tick, OnOff.OFF>,
  <Tick, OnOff.OFF, Tick, OnOff.OFF, Tick, OnOff.ON>,
  <Tick, OnOff.OFF, Tick, OnOff.ON,  Tick, OnOff.ON>,
  <Tick, OnOff.ON,  Tick, OnOff.OFF, Tick, OnOff.ON>,
  <Tick, OnOff.ON,  Tick, OnOff.ON,  Tick, OnOff.OFF>,
  <Tick, OnOff.ON,  Tick, OnOff.ON,  Tick, OnOff.ON>
]>>
component DelayTest(EventStream<OnOff> input, EventStream<OnOff> expected) {
  montiarc.timed.automata.Delay sut();

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitTimed<OnOff> generator(input);

  AssertEqualsTimed<OnOff> assertions(expected);
}
