/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.components;

import montiarc.rte.port.TimeUnawareInPort;
import montiarc.rte.port.TimeUnawareOutPort;

import java.util.List;

public interface IUntimedComponent extends IComponent<TimeUnawareInPort<?>, TimeUnawareOutPort<?>> {
  
  @Override
  List<TimeUnawareInPort<?>> getAllInPorts();
  
  @Override
  List<TimeUnawareOutPort<?>> getAllOutPorts();
}