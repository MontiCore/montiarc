/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.composition;

import montiarc.sync.automata.Parameter;
import montiarc.types.OnOff;

component ParameterForward(OnOff param = montiarc.types.OnOff.OFF) {

  port out OnOff o;

  Parameter comp(p=param);
  comp.o -> o;
}
