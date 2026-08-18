/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata.transition;

import montiarc.types.OnOff;
import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitSync;

<<test, ticks=[2,2,2,3], input=[
  Sync<int><>,
  Sync<1, 3>,
  Sync<-1, -4>,
  Sync<1, -3, 5>
], expected=[
  <OnOff><Tick, Tick>,
  <OnOff.ON, Tick, OnOff.ON, Tick>,
  <OnOff><Tick, Tick>,
  <OnOff.ON, Tick, Tick, OnOff.ON, Tick>
]>>
component IncompleteConditionTest(SyncStream<int> input, EventStream<OnOff> expected) {
  IncompleteCondition sut();

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitSync<Integer> generator(input);

  AssertEqualsTimed<OnOff> assertions(expected);
}
