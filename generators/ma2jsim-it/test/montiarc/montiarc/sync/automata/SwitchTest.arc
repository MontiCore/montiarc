/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

import montiarc.types.OnOff;
import montiarc.maunit.api.AssertEqualsUntimed;
import montiarc.maunit.api.EmitSync;

<<test={
  //i1                         i2                          o
  [Sync<OnOff.ON>,             Sync<OnOff.ON>,             Untimed<OnOff.ON>],
  [Sync<OnOff.OFF>,            Sync<OnOff.ON>,             Untimed<OnOff.OFF>],
  [Sync<OnOff.ON>,             Sync<OnOff.OFF>,            Untimed<OnOff.OFF>],
  [Sync<OnOff.OFF>,            Sync<OnOff.OFF>,            Untimed<OnOff.OFF>],
  [Sync<OnOff.ON, OnOff.ON>,   Sync<OnOff.OFF, OnOff.ON>,  Untimed<OnOff.OFF, OnOff.ON>],
  [Sync<OnOff.OFF, OnOff.ON>,  Sync<OnOff.OFF, OnOff.OFF>, Untimed<OnOff.OFF, OnOff.OFF>],
  [Sync<OnOff.ON, OnOff.OFF>,  Sync<OnOff.ON, OnOff.ON>,   Untimed<OnOff.ON, OnOff.OFF>],
  [Sync<OnOff.OFF, OnOff.OFF>, Sync<OnOff.ON, OnOff.OFF>,  Untimed<OnOff.OFF, OnOff.OFF>],
  [Sync<OnOff.OFF, OnOff.ON>,  Sync<OnOff.ON, OnOff.ON>,   Untimed<OnOff.OFF, OnOff.ON>],
  [Sync<OnOff.OFF, OnOff.OFF>, Sync<OnOff.OFF, OnOff.ON>,  Untimed<OnOff.OFF, OnOff.OFF>],
  [Sync<OnOff.ON, OnOff.ON>,   Sync<OnOff.ON, OnOff.OFF>,  Untimed<OnOff.ON, OnOff.OFF>],
  [Sync<OnOff.ON, OnOff.OFF>,  Sync<OnOff.OFF, OnOff.OFF>, Untimed<OnOff.OFF, OnOff.OFF>]
}, ticks=[1,1,1,1,2,2,2,2,2,2,2,2]>>
component SwitchTest(SyncStream<OnOff> i1, SyncStream<OnOff> i2, UntimedStream<OnOff> o) {
  Switch sut();

  generator1.out -> sut.i1;
  generator2.out -> sut.i2;
  sut.o -> assertions.actual;

  EmitSync<OnOff> generator1(i1), generator2(i2);

  AssertEqualsUntimed<OnOff> assertions(o);
}
