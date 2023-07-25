/* (c) https://github.com/MontiCore/monticore */
package montiarc.variability;

import montiarc.rte.port.TimeAwareOutPort;

import java.util.ArrayList;
import java.util.List;

public interface VariabilityOutput {
  
  default List<TimeAwareOutPort<?>> getAllOutPorts() {
    List<TimeAwareOutPort<?>> result = new ArrayList<>();
    
    if(port_o1_available()) result.add(port_o1());
    
    return result;
  }
  
  TimeAwareOutPort<Integer> port_o1();
  
  Boolean port_o1_available();
}
