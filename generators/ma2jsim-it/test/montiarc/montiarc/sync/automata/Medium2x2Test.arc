/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

import montiarc.types.OnOff;
import montiarc.maunit.api.AssertEqualsUntimed;
import montiarc.maunit.api.EmitSync;
import java.util.List;

<<test={
  //i1                    i2
  [[OnOff.ON],            [OnOff.ON]],
  [[OnOff.OFF],           [OnOff.ON]],
  [[OnOff.ON],            [OnOff.OFF]],
  [[OnOff.OFF],           [OnOff.OFF]],
  [[OnOff.ON, OnOff.ON],  [OnOff.OFF, OnOff.ON]],
  [[OnOff.OFF, OnOff.ON], [OnOff.OFF, OnOff.OFF]],
  [[OnOff.ON, OnOff.OFF], [OnOff.ON, OnOff.ON]],
  [[OnOff.OFF, OnOff.OFF],[OnOff.ON, OnOff.OFF]],
  [[OnOff.OFF, OnOff.ON], [OnOff.ON, OnOff.ON]],
  [[OnOff.OFF, OnOff.OFF],[OnOff.OFF, OnOff.ON]],
  [[OnOff.ON, OnOff.ON],  [OnOff.ON, OnOff.OFF]],
  [[OnOff.ON, OnOff.OFF], [OnOff.OFF, OnOff.OFF]]
}, ticks=[1,1,1,1,2,2,2,2,2,2,2,2]>>
component Medium2x2Test(List<OnOff> i1, List<OnOff> i2) {
  Medium2x2 sut;

  generator1.out -> sut.i1;
  generator2.out -> sut.i2;
  sut.o1 -> assertions1.actual;
  sut.o2 -> assertions2.actual;

  EmitSync<OnOff> generator1(i1), generator2(i2);

  AssertEqualsUntimed<OnOff> assertions1(i1), assertions2(i2);
}
