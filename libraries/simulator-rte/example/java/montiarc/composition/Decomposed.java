/* (c) https://github.com/MontiCore/monticore */
package montiarc.composition;

import montiarc.rte.component.ITimedComponent;
import montiarc.rte.port.TimeAwareInPort;
import montiarc.rte.port.TimeAwareOutPort;

import java.util.List;

public class Decomposed implements ITimedComponent {

  protected final String qualifiedInstanceName;

  public Decomposed(String qualifiedInstanceName) {
    this.qualifiedInstanceName = qualifiedInstanceName;

    this.sorter = new Sorter(this.qualifiedInstanceName + ".sorter");
    this.gtEq0 = new Counter(this.qualifiedInstanceName + ".gtEq0");
    this.lt0 = new Counter(this.qualifiedInstanceName + ".lt0");

    this.iIn = this.sorter.iIn;
    this.sorter.gtEq0.connect(this.gtEq0.iIn);
    this.sorter.lt0.connect(this.lt0.iIn);
  }

  @Override
  public String getQualifiedInstanceName() {
    return qualifiedInstanceName;
  }

  TimeAwareInPort<Integer> iIn;

  Sorter sorter;
  Counter gtEq0;
  Counter lt0;

  @Override
  public List<TimeAwareInPort<?>> getAllInPorts() {
    return List.of(iIn);
  }

  @Override
  public List<TimeAwareOutPort<?>> getAllOutPorts() {
    return List.of();
  }
}
