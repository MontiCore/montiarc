/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.composition;

import montiarc.types.OnOff;
import montiarc.maunit.api.AssertEqualsUntimed;
import montiarc.maunit.api.EmitSync;
import java.util.List;

<<test, ticks=[1,1,2,2,2,2,3], i=[
  [OnOff.ON],
  [OnOff.OFF],
  [OnOff.ON, OnOff.ON],
  [OnOff.OFF, OnOff.OFF],
  [OnOff.ON, OnOff.OFF],
  [OnOff.OFF, OnOff.ON],
  [OnOff.ON, OnOff.ON, OnOff.ON]
],o2=[
  [OnOff.OFF],
  [OnOff.ON],
  [OnOff.OFF, OnOff.OFF],
  [OnOff.ON, OnOff.ON],
  [OnOff.OFF, OnOff.ON],
  [OnOff.ON, OnOff.OFF],
  [OnOff.OFF, OnOff.OFF, OnOff.OFF]
]>>
component ParallelCompositionTest(List<OnOff> i, List<OnOff> o2) {
  ParallelComposition sut();

  generator.out -> sut.i1, sut.i2;
  sut.o1 -> assertions1.actual;
  sut.o2 -> assertions2.actual;

  EmitSync<OnOff> generator(i);

  AssertEqualsUntimed<OnOff> assertions1(i), assertions2(o2);
}
