/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

import montiarc.types.OnOff;
import montiarc.maunit.api.AssertEqualsUntimed;
import montiarc.maunit.api.EmitSync;

<<test={
  //i1                        i2
  [Sync<OnOff.ON>,            Sync<OnOff.ON>],
  [Sync<OnOff.OFF>,           Sync<OnOff.ON>],
  [Sync<OnOff.ON>,            Sync<OnOff.OFF>],
  [Sync<OnOff.OFF>,           Sync<OnOff.OFF>],
  [Sync<OnOff.ON, OnOff.ON>,  Sync<OnOff.OFF, OnOff.ON>],
  [Sync<OnOff.OFF, OnOff.ON>, Sync<OnOff.OFF, OnOff.OFF>],
  [Sync<OnOff.ON, OnOff.OFF>, Sync<OnOff.ON, OnOff.ON>],
  [Sync<OnOff.OFF, OnOff.OFF>,Sync<OnOff.ON, OnOff.OFF>],
  [Sync<OnOff.OFF, OnOff.ON>, Sync<OnOff.ON, OnOff.ON>],
  [Sync<OnOff.OFF, OnOff.OFF>,Sync<OnOff.OFF, OnOff.ON>],
  [Sync<OnOff.ON, OnOff.ON>,  Sync<OnOff.ON, OnOff.OFF>],
  [Sync<OnOff.ON, OnOff.OFF>, Sync<OnOff.OFF, OnOff.OFF>]
}, ticks=[1,1,1,1,2,2,2,2,2,2,2,2]>>
component Medium2x2Test(SyncStream<OnOff> i1, SyncStream<OnOff> i2) {
  Medium2x2 sut;

  generator1.out -> sut.i1;
  generator2.out -> sut.i2;
  sut.o1 -> assertions1.actual;
  sut.o2 -> assertions2.actual;

  EmitSync<OnOff> generator1(i1), generator2(i2);

  AssertEqualsUntimed<OnOff> assertions1(i1.untimed()), assertions2(i2.untimed());
}
