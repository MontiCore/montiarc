/* (c) https://github.com/MontiCore/monticore */
package montiarc.composition;

import montiarc.rte.component.ITimedComponent;
import montiarc.rte.port.AbstractInPort;
import montiarc.rte.port.ITimeAwareInPort;
import montiarc.rte.port.TimeAwareInPort;
import montiarc.rte.port.TimeAwareOutPort;

import java.util.List;

public class Decomposed implements ITimedComponent {

  protected final String name;

  public Decomposed(String name) {
    this.name = name;

    this.sorter = new Sorter(this.name + ".sorter");
    this.gtEq0 = new Counter(this.name + ".gtEq0");
    this.lt0 = new Counter(this.name + ".lt0");

    this.iIn = this.sorter.iIn;
    this.sorter.gtEq0.connect(this.gtEq0.iIn);
    this.sorter.lt0.connect(this.lt0.iIn);
  }

  @Override
  public String getName() {
    return name;
  }

  TimeAwareInPort<Integer> iIn;

  Sorter sorter;
  Counter gtEq0;
  Counter lt0;

  @Override
  public List<ITimeAwareInPort<?>> getAllInPorts() {
    return List.of(iIn);
  }

  @Override
  public List<TimeAwareOutPort<?>> getAllOutPorts() {
    return List.of();
  }
  
  @Override
  public void handleMessage(AbstractInPort<?> receivingPort) { /* nothing to do here because this is not a dynamic component */ }
}
