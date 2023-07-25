/* (c) https://github.com/MontiCore/monticore */
package montiarc.automata;

import montiarc.rte.port.TimeAwareOutPort;

import java.util.List;

public interface InverterOutput {
  
  default List<TimeAwareOutPort<?>> getAllOutPorts() {
    return List.of(port_o());
  }

  TimeAwareOutPort<Boolean> port_o();
}
