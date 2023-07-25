/* (c) https://github.com/MontiCore/monticore */
package montiarc.automata;

import montiarc.rte.port.TimeAwareOutPort;

import java.util.List;

public interface EventInverterOutput {
  
  default List<TimeAwareOutPort<?>> getAllOutPorts() {
    return List.of(port_bOut(), port_iOut());
  }

  TimeAwareOutPort<Boolean> port_bOut();
  
  TimeAwareOutPort<Integer> port_iOut();
}
