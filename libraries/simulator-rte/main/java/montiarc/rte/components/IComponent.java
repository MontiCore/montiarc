/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.components;

import montiarc.rte.port.IInPort;
import montiarc.rte.port.IOutPort;

import java.util.List;

public interface IComponent<InPort extends IInPort<?>, OutPort extends IOutPort<?>> {
  
  String getQualifiedInstanceName();
  
  List<InPort> getAllInPorts();
  
  List<OutPort> getAllOutPorts();
}