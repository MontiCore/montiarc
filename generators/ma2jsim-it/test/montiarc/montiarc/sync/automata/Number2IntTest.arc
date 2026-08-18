/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

import montiarc.maunit.api.AssertEqualsUntimed;
import montiarc.maunit.api.EmitSync;
import java.lang.Number;

<<test={
  // input                                           output
  [Sync<Number><0>,                                  Untimed<0>],
  [Sync<Number><1.5>,                                Untimed<1>],
  [Sync<Number><1.5, -1>,                            Untimed<1, -1>],
  [Sync<Number><1.0f, 3.333333, -1.6f>,              Untimed<1, 3, -1>]
}, ticks=[1, 1, 2, 3]>>
component Number2IntTest(SyncStream<Number> input, UntimedStream<int> output) {
  Number2Int<Number> sut();

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitSync<Number> generator(input);

  AssertEqualsUntimed<int> assertions(output);
}
