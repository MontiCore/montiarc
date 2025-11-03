/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang;

import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitSync;
import java.lang.Long;

<<test, ticks=[0,0,0,0, 1,1,1,1,1,1,1,1],
init=[Long.MIN_VALUE, Long.MIN_VALUE, Long.MAX_VALUE, Long.MAX_VALUE, Long.MIN_VALUE, Long.MIN_VALUE, Long.MIN_VALUE, Long.MIN_VALUE, Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE],
input=[
  Sync<Long.MIN_VALUE>,
  Sync<Long.MAX_VALUE>,
  Sync<Long.MIN_VALUE>,
  Sync<Long.MAX_VALUE>,
  Sync<Long.MIN_VALUE, Long.MIN_VALUE>,
  Sync<Long.MIN_VALUE, Long.MAX_VALUE>,
  Sync<Long.MAX_VALUE, Long.MIN_VALUE>,
  Sync<Long.MAX_VALUE, Long.MAX_VALUE>,
  Sync<Long.MIN_VALUE, Long.MIN_VALUE>,
  Sync<Long.MIN_VALUE, Long.MAX_VALUE>,
  Sync<Long.MAX_VALUE, Long.MIN_VALUE>,
  Sync<Long.MAX_VALUE, Long.MAX_VALUE>
], output=[
  <Long.MIN_VALUE>,
  <Long.MIN_VALUE>,
  <Long.MAX_VALUE>,
  <Long.MAX_VALUE>,
  <Long.MIN_VALUE, Tick, Long.MIN_VALUE>,
  <Long.MIN_VALUE, Tick, Long.MIN_VALUE>,
  <Long.MIN_VALUE, Tick, Long.MAX_VALUE>,
  <Long.MIN_VALUE, Tick, Long.MAX_VALUE>,
  <Long.MAX_VALUE, Tick, Long.MIN_VALUE>,
  <Long.MAX_VALUE, Tick, Long.MIN_VALUE>,
  <Long.MAX_VALUE, Tick, Long.MAX_VALUE>,
  <Long.MAX_VALUE, Tick, Long.MAX_VALUE>
]>>
component TSDelayLongTest(long init, SyncStream<long> input, EventStream<long> output) {
  TSDelayLong sut(init);

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitSync<long> generator(input);

  AssertEqualsTimed<long> assertions(output);
}
