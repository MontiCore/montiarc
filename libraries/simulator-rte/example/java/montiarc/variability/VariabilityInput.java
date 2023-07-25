/* (c) https://github.com/MontiCore/monticore */
package montiarc.variability;

import montiarc.rte.port.TimeAwareInPort;

import java.util.ArrayList;
import java.util.List;

public interface VariabilityInput {
  
  default List<TimeAwareInPort<?>> getAllInPorts() {
    List<TimeAwareInPort<?>> result = new ArrayList<>();
    
    if(port_i1_available()) result.add(port_i1());
  
    if(port_i2_available()) result.add(port_i2());
    
    if(port_i3_available()) result.add(port_i3());
    
    if(port_i4_available()) result.add(port_i4());
    
    return result;
  }
  
  TimeAwareInPort<Integer> port_i1();

  Boolean port_i1_available();
  
  TimeAwareInPort<Integer> port_i2();

  Boolean port_i2_available();
  
  TimeAwareInPort<Integer> port_i3();

  Boolean port_i3_available();
  
  TimeAwareInPort<Integer> port_i4();

  Boolean port_i4_available();
}
