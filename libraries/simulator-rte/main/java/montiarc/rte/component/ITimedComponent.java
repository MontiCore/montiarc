/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.component;

import montiarc.rte.port.TimeAwareInPort;
import montiarc.rte.port.TimeAwareOutPort;

import java.util.List;

public interface ITimedComponent extends IComponent<TimeAwareInPort<?>, TimeAwareOutPort<?>> {

  @Override
  List<TimeAwareInPort<?>> getAllInPorts();

  @Override
  List<TimeAwareOutPort<?>> getAllOutPorts();

}
