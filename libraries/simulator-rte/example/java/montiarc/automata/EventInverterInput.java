/* (c) https://github.com/MontiCore/monticore */
package montiarc.automata;

import montiarc.rte.port.TimeAwareInPort;

import java.util.List;

public interface EventInverterInput {
  
  default List<TimeAwareInPort<?>> getAllInPorts() {
    return List.of(port_bIn(), port_iIn());
  }
  
  TimeAwareInPort<Boolean> port_bIn();
  
  TimeAwareInPort<Integer> port_iIn();
}
