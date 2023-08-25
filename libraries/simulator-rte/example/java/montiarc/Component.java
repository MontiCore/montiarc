/* (c) https://github.com/MontiCore/monticore */
package montiarc;

import montiarc.rte.component.ITimedComponent;
import montiarc.rte.port.AbstractInPort;
import montiarc.rte.port.ITimeAwareInPort;
import montiarc.rte.port.TimeAwareInPort;
import montiarc.rte.port.TimeAwareOutPort;
import montiarc.rte.msg.Message;

import java.util.List;


//used in doc as an example
public class Component implements ITimedComponent {

  protected final String name;

  // constructor, etc...
  public Component(String name) {
    this.name = name;
  }

  private boolean componentCanCompute() {
    // perform arbitrary checks here
    return true;
  }

  AbstractInPort<String> incomingPort = new AbstractInPort<>(getName() + ".portName", this) {

    @Override
    public boolean messageIsValidOnPort(Message<String> message) {
      return false;
    }
  };
  
  @Override
  public String getName() {
    return name;
  }
  
  @Override
  public List<ITimeAwareInPort<?>> getAllInPorts() {
    return List.of();
  }

  @Override
  public List<TimeAwareOutPort<?>> getAllOutPorts() {
    return List.of();
  }
  
  @Override
  public void handleMessage(AbstractInPort<?> receivingPort) { /* nothing to implement in this example */}
}
