/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.composition;

import montiarc.types.OnOff;
import montiarc.maunit.api.AssertEqualsTimed;
import montiarc.maunit.api.EmitTimed;
import java.util.List;

<<test, ticks=[1,1,1,1,1,1,1, 2,2,2,2,2,2,2,2,2,2,2, 3,3,3,3,3,3,3,3,3,3,3,3,3,3],
input=[
  [[OnOff.ON]],
  [[OnOff.OFF]],
  [[OnOff.ON, OnOff.ON]],
  [[OnOff.ON, OnOff.OFF]],
  [[OnOff.OFF, OnOff.ON]],
  [[OnOff.OFF, OnOff.OFF]],
  [[]],
  [[], [OnOff.ON]],
  [[], [OnOff.OFF]],
  [[], [OnOff.ON, OnOff.ON]],
  [[], [OnOff.ON, OnOff.OFF]],
  [[], [OnOff.OFF, OnOff.ON]],
  [[], [OnOff.OFF, OnOff.OFF]],
  [[OnOff.ON], [OnOff.ON]],
  [[OnOff.ON], [OnOff.OFF]],
  [[OnOff.OFF], [OnOff.ON]],
  [[OnOff.OFF], [OnOff.OFF]],
  [[], []],
  [[OnOff.ON], [], []],
  [[OnOff.OFF], [], []],
  [[], [OnOff.ON], []],
  [[], [OnOff.OFF], []],
  [[], [], [OnOff.ON]],
  [[], [], [OnOff.OFF]],
  [[OnOff.ON], [OnOff.ON], [OnOff.ON]],
  [[OnOff.ON], [OnOff.ON], [OnOff.OFF]],
  [[OnOff.ON], [OnOff.OFF], [OnOff.ON]],
  [[OnOff.ON], [OnOff.OFF], [OnOff.OFF]],
  [[OnOff.OFF], [OnOff.ON], [OnOff.ON]],
  [[OnOff.OFF], [OnOff.ON], [OnOff.OFF]],
  [[OnOff.OFF], [OnOff.OFF], [OnOff.ON]],
  [[OnOff.OFF], [OnOff.OFF], [OnOff.OFF]]
], expected=[
  [[OnOff.OFF]],
  [[OnOff.ON]],
  [[OnOff.OFF, OnOff.OFF]],
  [[OnOff.OFF, OnOff.ON]],
  [[OnOff.ON, OnOff.OFF]],
  [[OnOff.ON, OnOff.ON]],
  [[]],
  [[], [OnOff.OFF]],
  [[], [OnOff.ON]],
  [[], [OnOff.OFF, OnOff.OFF]],
  [[], [OnOff.OFF, OnOff.ON]],
  [[], [OnOff.ON, OnOff.OFF]],
  [[], [OnOff.ON, OnOff.ON]],
  [[OnOff.OFF], [OnOff.OFF]],
  [[OnOff.OFF], [OnOff.ON]],
  [[OnOff.ON], [OnOff.OFF]],
  [[OnOff.ON], [OnOff.ON]],
  [[], []],
  [[OnOff.OFF], [], []],
  [[OnOff.ON], [], []],
  [[], [OnOff.OFF], []],
  [[], [OnOff.ON], []],
  [[], [], [OnOff.OFF]],
  [[], [], [OnOff.ON]],
  [[OnOff.OFF], [OnOff.OFF], [OnOff.OFF]],
  [[OnOff.OFF], [OnOff.OFF], [OnOff.ON]],
  [[OnOff.OFF], [OnOff.ON], [OnOff.OFF]],
  [[OnOff.OFF], [OnOff.ON], [OnOff.ON]],
  [[OnOff.ON], [OnOff.OFF], [OnOff.OFF]],
  [[OnOff.ON], [OnOff.OFF], [OnOff.ON]],
  [[OnOff.ON], [OnOff.ON], [OnOff.OFF]],
  [[OnOff.ON], [OnOff.ON], [OnOff.ON]]
]>>
component SequentialCompositionTest(List<List<OnOff>> input, List<List<OnOff>> expected) {
  SequentialComposition sut();

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitTimed<OnOff> generator(input);
  AssertEqualsTimed<OnOff> assertions(expected);
}
