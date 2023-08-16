/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.component;

import montiarc.rte.port.ITimeAwareInPort;
import montiarc.rte.port.TimeAwareOutPort;

import java.util.List;

public interface ITimedComponent extends IComponent<ITimeAwareInPort<?>, TimeAwareOutPort<?>> {

  @Override
  List<ITimeAwareInPort<?>> getAllInPorts();

  @Override
  List<TimeAwareOutPort<?>> getAllOutPorts();

}
