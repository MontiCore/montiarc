/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.composition;

import montiarc.types.OnOff;
import montiarc.maunit.api.AssertEquals;
import java.util.List;

<<test, ticks=1, p=[
  0,
  2000,
  -1
]>>
component PrimitiveParameterForwardTest(int p) {
  PrimitiveParameterForward sut(p);

  sut.o -> assertions.actual;

  AssertEquals<int> assertions(p);
}
