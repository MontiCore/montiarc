/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

import montiarc.types.OnOff;
import montiarc.maunit.api.AssertEquals;

<<test, ticks=[2,2], p=[OnOff.OFF, OnOff.ON]>>
component ParameterTest(OnOff p) {
  Parameter sut(p);

  sut.o -> assertions.actual;

  AssertEquals<OnOff> assertions(p);
}
