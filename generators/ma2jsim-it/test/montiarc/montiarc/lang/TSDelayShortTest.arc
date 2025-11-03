/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang;

import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitSync;
import java.lang.Short;

<<test, ticks=[0,0,0,0, 1,1,1,1,1,1,1,1],
init=[Short.MIN_VALUE, Short.MIN_VALUE, Short.MAX_VALUE, Short.MAX_VALUE, Short.MIN_VALUE, Short.MIN_VALUE, Short.MIN_VALUE, Short.MIN_VALUE, Short.MAX_VALUE, Short.MAX_VALUE, Short.MAX_VALUE, Short.MAX_VALUE],
input=[
  Sync<Short.MIN_VALUE>,
  Sync<Short.MAX_VALUE>,
  Sync<Short.MIN_VALUE>,
  Sync<Short.MAX_VALUE>,
  Sync<Short.MIN_VALUE, Short.MIN_VALUE>,
  Sync<Short.MIN_VALUE, Short.MAX_VALUE>,
  Sync<Short.MAX_VALUE, Short.MIN_VALUE>,
  Sync<Short.MAX_VALUE, Short.MAX_VALUE>,
  Sync<Short.MIN_VALUE, Short.MIN_VALUE>,
  Sync<Short.MIN_VALUE, Short.MAX_VALUE>,
  Sync<Short.MAX_VALUE, Short.MIN_VALUE>,
  Sync<Short.MAX_VALUE, Short.MAX_VALUE>
], output=[
  <Short.MIN_VALUE>,
  <Short.MIN_VALUE>,
  <Short.MAX_VALUE>,
  <Short.MAX_VALUE>,
  <Short.MIN_VALUE, Tick, Short.MIN_VALUE>,
  <Short.MIN_VALUE, Tick, Short.MIN_VALUE>,
  <Short.MIN_VALUE, Tick, Short.MAX_VALUE>,
  <Short.MIN_VALUE, Tick, Short.MAX_VALUE>,
  <Short.MAX_VALUE, Tick, Short.MIN_VALUE>,
  <Short.MAX_VALUE, Tick, Short.MIN_VALUE>,
  <Short.MAX_VALUE, Tick, Short.MAX_VALUE>,
  <Short.MAX_VALUE, Tick, Short.MAX_VALUE>
]>>
component TSDelayShortTest(short init, SyncStream<short> input, EventStream<short> output) {
  TSDelayShort sut(init);

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitSync<short> generator(input);

  AssertEqualsTimed<short> assertions(output);
}
