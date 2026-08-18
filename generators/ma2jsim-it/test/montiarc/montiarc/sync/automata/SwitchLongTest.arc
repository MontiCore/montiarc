/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

import montiarc.maunit.api.AssertEqualsUntimed;
import montiarc.maunit.api.EmitSync;

<<test={
  // input                        expected
  [Sync<0L>,                      Untimed<10>],
  [Sync<1L>,                      Untimed<20>],
  [Sync<2L>,                      Untimed<-1>],
  [Sync<100L>,                    Untimed<-1>],
  [Sync<0L, 1L, 7L>,              Untimed<10, 20, -1>]
}, ticks=[1, 1, 1, 1, 3]>>
component SwitchLongTest(SyncStream<long> input, UntimedStream<int> expected) {
  SwitchLong sut();

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitSync<long> generator(input);

  AssertEqualsUntimed<int> assertions(expected);
}
