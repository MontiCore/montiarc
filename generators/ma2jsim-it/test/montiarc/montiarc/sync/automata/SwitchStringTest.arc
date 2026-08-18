/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

import montiarc.maunit.api.AssertEqualsUntimed;
import montiarc.maunit.api.EmitSync;
import java.lang.String;

<<test={
  // input                                                      expected
  [Sync<String><"hello">,                                       Untimed<1>],
  [Sync<String><"world">,                                       Untimed<2>],
  [Sync<String><"test">,                                        Untimed<3>],
  [Sync<String><"unknown">,                                     Untimed<0>],
  [Sync<String><"">,                                            Untimed<0>],
  [Sync<String><"hello", "world", "other">,                     Untimed<1, 2, 0>]
}, ticks=[1, 1, 1, 1, 1, 3]>>
component SwitchStringTest(SyncStream<String> input, UntimedStream<int> expected) {
  SwitchString sut();

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitSync<String> generator(input);

  AssertEqualsUntimed<int> assertions(expected);
}
