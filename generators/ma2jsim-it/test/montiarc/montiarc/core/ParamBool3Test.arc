/* (c) https://github.com/MontiCore/monticore */
package montiarc.core;

import montiarc.maunit.api.AssertEqualsUntimed;
import montiarc.maunit.api.EmitList;
import java.util.List;

<<test, p=[
  true, true, false, false, true, true, true, true, false, false, false, false
], input=[
  [true], [false], [true], [false],
  [true, true], [true, false], [false, true], [false, false],
  [true, true], [true, false], [false, true], [false, false]
]>>
component ParamBool3Test(boolean p, List<boolean> input) {
  ParamBool3 sut(p);

  generator.out -> sut.i;
  sut.o -> assertions.actual;

  EmitList<boolean> generator(input);

  AssertEqualsUntimed<boolean> assertions(input);
}
