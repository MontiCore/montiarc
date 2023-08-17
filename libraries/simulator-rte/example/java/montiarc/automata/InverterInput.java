/* (c) https://github.com/MontiCore/monticore */
package montiarc.automata;

import montiarc.rte.port.ITimeAwareInPort;
import montiarc.rte.port.TimeAwareInPort;

import java.util.List;

public interface InverterInput {
  
  default List<ITimeAwareInPort<?>> getAllInPorts() {
    return List.of(port_i());
  }

  TimeAwareInPort<Boolean> port_i();
}