/* (c) https://github.com/MontiCore/monticore */
package montiarc.core;

import montiarc.maunit.api.AssertEqualsUntimed;
import montiarc.maunit.api.EmitList;
import java.util.List;

<<test={
  [true, true, [true], [true], [true]],
  [true, true, [false], [false], [false]],
  [true, false, [true], [true], []],
  [true, false, [false], [false], []],
  [false, true, [true], [], [true]],
  [false, true, [false], [], [false]],
  [false, false, [true], [], []],
  [false, false, [false], [], []],
  [true, true, [true, true], [true, true], [true, true]],
  [true, true, [true, false], [true, false], [true, false]],
  [true, true, [false, true], [false, true], [false, true]],
  [true, true, [false, false], [false, false], [false, false]],
  [true, false, [true, true], [true, true], []],
  [true, false, [true, false], [true, false], []],
  [true, false, [false, true], [false, true], []],
  [true, false, [false, false], [false, false], []],
  [false, true, [true, true], [], [true, true]],
  [false, true, [true, false], [], [true, false]],
  [false, true, [false, true], [], [false, true]],
  [false, true, [false, false], [], [false, false]],
  [false, false, [true, true], [], []],
  [false, false, [true, false], [], []],
  [false, false, [false, true], [], []],
  [false, false, [false, false], [], []]
}>>
component ParamsBool3Test(boolean p1, boolean p2, List<boolean> input, List<boolean> expected1, List<boolean> expected2) {
  ParamsBool3 sut(p1, p2);

  generator.out -> sut.i;
  sut.o1 -> assertions1.actual;
  sut.o2 -> assertions2.actual;

  EmitList<boolean> generator(input);

  AssertEqualsUntimed<boolean> assertions1(expected1), assertions2(expected2);
}
