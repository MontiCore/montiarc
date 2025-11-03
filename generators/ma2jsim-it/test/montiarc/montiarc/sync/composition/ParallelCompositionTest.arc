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
],o2=[
  Untimed<OnOff.OFF>,
  Untimed<OnOff.ON>,
  Untimed<OnOff.OFF, OnOff.OFF>,
  Untimed<OnOff.ON, OnOff.ON>,
  Untimed<OnOff.OFF, OnOff.ON>,
  Untimed<OnOff.ON, OnOff.OFF>,
  Untimed<OnOff.OFF, OnOff.OFF, OnOff.OFF>
]>>
component ParallelCompositionTest(SyncStream<OnOff> i, UntimedStream<OnOff> o2) {
  ParallelComposition sut();

  generator.out -> sut.i1, sut.i2;
  sut.o1 -> assertions1.actual;
  sut.o2 -> assertions2.actual;

  EmitSync<OnOff> generator(i);

  AssertEqualsUntimed<OnOff> assertions1(i.untimed()), assertions2(o2);
}
