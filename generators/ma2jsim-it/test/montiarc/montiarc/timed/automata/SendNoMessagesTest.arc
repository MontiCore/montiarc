/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata;

import montiarc.types.OnOff;
import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitTimed;

<<test, ticks=[1,2,1,2,2], input=[
  <OnOff><Tick>,
  <Tick, OnOff.ON, Tick>,
  <OnOff.ON, Tick>,
  <Tick, OnOff.ON, Tick>,
  <OnOff.ON, Tick, OnOff.ON, Tick>
], expected=[
  <OnOff><Tick>,
  <OnOff><Tick, Tick>,
  <OnOff><Tick>,
  <OnOff><Tick, Tick>,
  <OnOff><Tick, Tick>
]>>
component SendNoMessagesTest(EventStream<OnOff> input, EventStream<OnOff> expected) {
  montiarc.timed.automata.SendNoMessages sut();

  generator.out -> sut.p;
  sut.o -> assertions.actual;

  EmitTimed<OnOff> generator(input);

  AssertEqualsTimed<OnOff> assertions(expected);
}
