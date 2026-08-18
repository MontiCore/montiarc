/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

import montiarc.maunit.api.AssertEqualsUntimed;
import montiarc.maunit.api.EmitSync;
import java.lang.Long;

<<test={
  // input                                 expected
  [Sync<100L>,                             Untimed<30>],
  [Sync<200L>,                             Untimed<40>],
  [Sync<150L>,                             Untimed<-1>],
  [Sync<300L>,                             Untimed<-1>],
  [Sync<100L, 200L, 999L>,                 Untimed<30, 40, -1>]
}, ticks=[1, 1, 1, 1, 3]>>
component SwitchBoxedLongTest(SyncStream<Long> input, UntimedStream<int> expected) {
  SwitchBoxedLong sut();

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitSync<Long> generator(input);

  AssertEqualsUntimed<int> assertions(expected);
}
