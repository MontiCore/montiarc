/* (c) https://github.com/MontiCore/monticore */
package montiarc;

import montiarc.rte.component.ITimedComponent;
import montiarc.rte.port.ITimeAwareInPort;
import montiarc.rte.port.TimeAwareInPort;
import montiarc.rte.port.TimeAwareOutPort;

import java.util.List;

//used in doc as an example
public class Inner implements ITimedComponent {

  protected final String name;

  public Inner(String name) {
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public List<ITimeAwareInPort<?>> getAllInPorts() {
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
