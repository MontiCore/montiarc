/* (c) https://github.com/MontiCore/monticore */
package montiarc.mixed_timing.composition;

import montiarc.types.OnOff;
import montiarc.maunit.api.AssertEqualsTimed;

<<test, ticks=[0, 1, 2], expected=[
  <OnOff.OFF, Tick>,
  <OnOff.OFF, Tick, OnOff.OFF, Tick>,
  <OnOff.OFF, Tick, OnOff.OFF, Tick, OnOff.OFF, Tick>
]>>
component FeedbackLoopTest(EventStream<OnOff> expected) {
  FeedbackLoop sut;

  sut.o -> assertions.actual;

  AssertEqualsTimed<OnOff> assertions(expected);
}
