/* (c) https://github.com/MontiCore/monticore */
package montiarc;

import montiarc.rte.component.ITimedComponent;
import montiarc.rte.port.ITimeAwareInPort;
import montiarc.rte.port.TimeAwareInPort;
import montiarc.rte.port.TimeAwareOutPort;

import java.util.List;

//used in doc as an example
public class Setup implements ITimedComponent {

  protected final String name;

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

  TimeAwareInPort<String> sIn;
  TimeAwareOutPort<String> sOut;

  Inner i1;
  Inner i2;

  public Setup(String name) {
    this.name = name;

    this.i1 = new Inner("DELETEME");
    this.i2 = new Inner("DELETEME");

    this.sIn = this.i1.sIn;
    this.i1.sOut.connect(this.i2.sIn);
    this.sOut = this.i2.sOut;
  }
}
