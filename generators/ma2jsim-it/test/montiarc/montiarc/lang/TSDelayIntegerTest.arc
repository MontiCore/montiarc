/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang;

import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitSync;
import java.lang.Integer;

<<test, ticks=[0,0,0,0, 1,1,1,1,1,1,1,1],
init=[Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE],
input=[
  Sync<Integer.MIN_VALUE>,
  Sync<Integer.MAX_VALUE>,
  Sync<Integer.MIN_VALUE>,
  Sync<Integer.MAX_VALUE>,
  Sync<Integer.MIN_VALUE, Integer.MIN_VALUE>,
  Sync<Integer.MIN_VALUE, Integer.MAX_VALUE>,
  Sync<Integer.MAX_VALUE, Integer.MIN_VALUE>,
  Sync<Integer.MAX_VALUE, Integer.MAX_VALUE>,
  Sync<Integer.MIN_VALUE, Integer.MIN_VALUE>,
  Sync<Integer.MIN_VALUE, Integer.MAX_VALUE>,
  Sync<Integer.MAX_VALUE, Integer.MIN_VALUE>,
  Sync<Integer.MAX_VALUE, Integer.MAX_VALUE>
], output=[
  <Integer.MIN_VALUE>,
  <Integer.MIN_VALUE>,
  <Integer.MAX_VALUE>,
  <Integer.MAX_VALUE>,
  <Integer.MIN_VALUE, Tick, Integer.MIN_VALUE>,
  <Integer.MIN_VALUE, Tick, Integer.MIN_VALUE>,
  <Integer.MIN_VALUE, Tick, Integer.MAX_VALUE>,
  <Integer.MIN_VALUE, Tick, Integer.MAX_VALUE>,
  <Integer.MAX_VALUE, Tick, Integer.MIN_VALUE>,
  <Integer.MAX_VALUE, Tick, Integer.MIN_VALUE>,
  <Integer.MAX_VALUE, Tick, Integer.MAX_VALUE>,
  <Integer.MAX_VALUE, Tick, Integer.MAX_VALUE>
]>>
component TSDelayIntegerTest(int init, SyncStream<int> input, EventStream<int> output) {
  TSDelayInteger sut(init);

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitSync<int> generator(input);

  AssertEqualsTimed<int> assertions(output);
}
