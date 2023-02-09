/* (c) https://github.com/MontiCore/monticore */
package montiarc;

import montiarc.rte.components.ITimedComponent;
import montiarc.rte.port.TimeAwareInPort;
import montiarc.rte.port.TimeAwareOutPort;

import java.util.List;

//used in doc as an example
public class Inner implements ITimedComponent {
  
  protected final String qualifiedInstanceName;
  
  public Inner(String qualifiedInstanceName) {
    this.qualifiedInstanceName = qualifiedInstanceName;
  }
  
  @Override
  public String getQualifiedInstanceName() {
    return qualifiedInstanceName;
  }
  
  @Override
  public List<TimeAwareInPort<?>> getAllInPorts() {
    return List.of(sIn);
  }
  
  @Override
  public List<TimeAwareOutPort<?>> getAllOutPorts() {
    return List.of(sOut);
  }
  
  TimeAwareInPort<String> sIn = new TimeAwareInPort<>("DELETEME") {
    @Override
    protected void handleBuffer() {
    
    }
  };
  
  TimeAwareOutPort<String> sOut = new TimeAwareOutPort<>("DELETEME");
  
  // behavior...
}
