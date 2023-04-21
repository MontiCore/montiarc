/* (c) https://github.com/MontiCore/monticore */
package montiarc;

import montiarc.rte.component.ITimedComponent;
import montiarc.rte.port.AbstractInPort;
import montiarc.rte.port.TimeAwareInPort;
import montiarc.rte.port.TimeAwareOutPort;
import montiarc.rte.msg.Message;

import java.util.List;


//used in doc as an example
public class Component implements ITimedComponent {

  protected final String qualifiedInstanceName;

  // constructor, etc...
  public Component(String qualifiedInstanceName) {
    this.qualifiedInstanceName = qualifiedInstanceName;
  }

  private boolean componentCanCompute() {
    // perform arbitrary checks here
    return true;
  }

  AbstractInPort<String> incomingPort = new AbstractInPort<>(getQualifiedInstanceName() + ".portName") {
    @Override
    protected void handleBuffer() {
      if (componentCanCompute()) {
        // trigger the computation
      }
    }

    @Override
    public boolean messageIsValidOnPort(Message<String> message) {
      return false;
    }
  };

  @Override
  public String getQualifiedInstanceName() {
    return qualifiedInstanceName;
  }

  @Override
  public List<TimeAwareInPort<?>> getAllInPorts() {
    return List.of();
  }

  @Override
  public List<TimeAwareOutPort<?>> getAllOutPorts() {
    return List.of();
  }
}
