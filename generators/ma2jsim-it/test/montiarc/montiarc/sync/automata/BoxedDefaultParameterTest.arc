/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

import montiarc.maunit.api.AssertEquals;

<<test, ticks=2, param=[5.0, 2.5]>>
component BoxedDefaultParameterTest(Double param) {
  BoxedDefaultParameter sut(param);

  sut.o -> assertDouble.actual;

  AssertEquals<Double> assertDouble(param);
}
