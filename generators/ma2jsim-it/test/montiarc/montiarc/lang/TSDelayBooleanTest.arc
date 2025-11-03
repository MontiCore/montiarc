/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang;

import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitSync;

<<test, ticks=[0,0,0,0, 1,1,1,1,1,1,1,1],
init=[true, true, false, false, true, true, true, true, false, false, false, false],
input=[
  Sync<true>,
  Sync<false>,
  Sync<true>,
  Sync<false>,
  Sync<true, true>,
  Sync<true, false>,
  Sync<false, true>,
  Sync<false, false>,
  Sync<true, true>,
  Sync<true, false>,
  Sync<false, true>,
  Sync<false, false>
], output=[
  <true>,
  <true>,
  <false>,
  <false>,
  <true, Tick, true>,
  <true, Tick, true>,
  <true, Tick, false>,
  <true, Tick, false>,
  <false, Tick, true>,
  <false, Tick, true>,
  <false, Tick, false>,
  <false, Tick, false>
]>>
component TSDelayBooleanTest(boolean init, SyncStream<boolean> input, EventStream<boolean> output) {
  TSDelayBoolean sut(init);

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitSync<boolean> generator(input);

  AssertEqualsTimed<boolean> assertions(output);
}
