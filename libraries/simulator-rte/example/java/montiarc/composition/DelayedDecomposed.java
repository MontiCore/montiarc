/* (c) https://github.com/MontiCore/monticore */
package montiarc.composition;

import montiarc.rte.component.ITimedComponent;
import montiarc.rte.port.TimeAwareInPort;
import montiarc.rte.port.TimeAwareOutPort;

import java.util.List;

public class DelayedDecomposed implements ITimedComponent {

  protected final String name;

  @Override
  public String getName() {
    return name;
  }

  @Override
  public List<TimeAwareInPort<?>> getAllInPorts() {
    return List.of(iIn);
  }

  @Override
  public List<TimeAwareOutPort<?>> getAllOutPorts() {
    return List.of();
  }

  TimeAwareInPort<Integer> iIn;

  DelayedSorter delayedSorter;
  Counter gtEq0;
  Counter lt0;

  public DelayedDecomposed(String name) {
    this.name = name;

    this.delayedSorter = new DelayedSorter(getName() + ".delayedSorter");
    this.gtEq0 = new Counter(getName() + ".gtEq0");
    this.lt0 = new Counter(getName() + ".lt0");

    this.iIn = this.delayedSorter.iIn;
    this.delayedSorter.gtEq0.connect(this.gtEq0.iIn);
    this.delayedSorter.lt0.connect(this.lt0.iIn);
  }
}
