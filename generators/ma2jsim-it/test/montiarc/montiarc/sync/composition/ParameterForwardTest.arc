/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.composition;

import montiarc.types.OnOff;
import montiarc.maunit.api.AssertEquals;

<<test, ticks=1, p=[
  OnOff.OFF,
  OnOff.ON
]>>
component ParameterForwardTest(OnOff p) {
  ParameterForward sut(p);

  sut.o -> assertions.actual;

  AssertEquals<OnOff> assertions(p);
}
