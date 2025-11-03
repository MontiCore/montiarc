/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.composition;

import montiarc.types.OnOff;
import montiarc.maunit.api.AssertEqualsUntimed;
import montiarc.maunit.api.EmitSync;

<<test, ticks=[1,1,2,2,2,2,3], i=[
  Sync<OnOff.ON>,
  Sync<OnOff.OFF>,
  Sync<OnOff.ON, OnOff.ON>,
  Sync<OnOff.OFF, OnOff.OFF>,
  Sync<OnOff.ON, OnOff.OFF>,
  Sync<OnOff.OFF, OnOff.ON>,
  Sync<OnOff.ON, OnOff.ON, OnOff.ON>
], o=[
  Untimed<OnOff.OFF>,
  Untimed<OnOff.ON>,
  Untimed<OnOff.OFF, OnOff.OFF>,
  Untimed<OnOff.ON, OnOff.ON>,
  Untimed<OnOff.OFF, OnOff.ON>,
  Untimed<OnOff.ON, OnOff.OFF>,
  Untimed<OnOff.OFF, OnOff.OFF, OnOff.OFF>
]>>
component SequentialCompositionTest(SyncStream<OnOff> i, UntimedStream<OnOff> o) {
  SequentialComposition sut();

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitSync<OnOff> generator(i);

  AssertEqualsUntimed<OnOff> assertions(o);
}
