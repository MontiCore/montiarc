/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.compute;

import montiarc.types.OnOff;
import montiarc.maunit.api.AssertEqualsUntimed;
import montiarc.maunit.api.EmitSync;
import java.util.List;

<<test={
  //i1                     i2                      o
  [[OnOff.ON],             [OnOff.ON],             [OnOff.ON]],
  [[OnOff.OFF],            [OnOff.ON],             [OnOff.OFF]],
  [[OnOff.ON],             [OnOff.OFF],            [OnOff.OFF]],
  [[OnOff.OFF],            [OnOff.OFF],            [OnOff.OFF]],
  [[OnOff.ON, OnOff.ON],   [OnOff.OFF, OnOff.ON],  [OnOff.OFF, OnOff.ON]],
  [[OnOff.OFF, OnOff.ON],  [OnOff.OFF, OnOff.OFF], [OnOff.OFF, OnOff.OFF]],
  [[OnOff.ON, OnOff.OFF],  [OnOff.ON, OnOff.ON],   [OnOff.ON, OnOff.OFF]],
  [[OnOff.OFF, OnOff.OFF], [OnOff.ON, OnOff.OFF],  [OnOff.OFF, OnOff.OFF]],
  [[OnOff.OFF, OnOff.ON],  [OnOff.ON, OnOff.ON],   [OnOff.OFF, OnOff.ON]],
  [[OnOff.OFF, OnOff.OFF], [OnOff.OFF, OnOff.ON],  [OnOff.OFF, OnOff.OFF]],
  [[OnOff.ON, OnOff.ON],   [OnOff.ON, OnOff.OFF],  [OnOff.ON, OnOff.OFF]],
  [[OnOff.ON, OnOff.OFF],  [OnOff.OFF, OnOff.OFF], [OnOff.OFF, OnOff.OFF]]
}, ticks=[1,1,1,1,2,2,2,2,2,2,2,2]>>
component SwitchTest(List<OnOff> i1, List<OnOff> i2, List<OnOff> o) {
  Switch sut();

  generator1.out -> sut.i1;
  generator2.out -> sut.i2;
  sut.o -> assertions.actual;

  EmitSync<OnOff> generator1(i1), generator2(i2);

  AssertEqualsUntimed<OnOff> assertions(o);
}
