/* (c) https://github.com/MontiCore/monticore */
package montiarc.core;

import montiarc.maunit.api.AssertEqualsUntimed;
import montiarc.maunit.api.EmitList;
import java.util.List;

<<test, p1=[
  true, true, true, true, false, false, false, false, true, true, false, false
], p2=[
  true, true, false, false, true, true, false, false, true, false, true, false
], input=[
  [true], [false], [true], [false], [true], [false], [true], [false],
  [true, true], [true, true], [true, true], [true, true]
], expected1=[
  [true], [true], [true], [true], [false], [false], [false], [false],
  [true, true], [true, true], [false, false], [false, false]
], expected2=[
  [true], [true], [false], [false], [true], [true], [false], [false],
  [true, true], [false, false], [true, true], [false, false]
]>>
component ParamsBool1Test(boolean p1, boolean p2, List<boolean> input, List<boolean> expected1, List<boolean> expected2) {
  ParamsBool1 sut(p1, p2);

  generator.out -> sut.i;
  sut.o1 -> assertions1.actual;
  sut.o2 -> assertions2.actual;

  EmitList<boolean> generator(input);

  AssertEqualsUntimed<boolean> assertions1(expected1), assertions2(expected2);
}
