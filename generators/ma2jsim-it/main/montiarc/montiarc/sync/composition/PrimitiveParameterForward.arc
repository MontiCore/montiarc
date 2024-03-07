/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.composition;

import montiarc.sync.automata.PrimitiveParameter;
import montiarc.types.OnOff;

component PrimitiveParameterForward(int param = 3) {

  port out int o;

  PrimitiveParameter comp(p=param);
  comp.o -> o;
}
