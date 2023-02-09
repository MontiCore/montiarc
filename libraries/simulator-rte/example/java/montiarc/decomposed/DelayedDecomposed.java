/* (c) https://github.com/MontiCore/monticore */
package montiarc.decomposed;

import montiarc.rte.components.ITimedComponent;
import montiarc.rte.port.TimeAwareInPort;
import montiarc.rte.port.TimeAwareOutPort;

import java.util.List;

public class DelayedDecomposed implements ITimedComponent {
  
  protected final String qualifiedInstanceName;
  
  @Override
  public String getQualifiedInstanceName() {
    return qualifiedInstanceName;
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
  
  public DelayedDecomposed(String qualifiedInstanceName) {
    this.qualifiedInstanceName = qualifiedInstanceName;
    
    this.delayedSorter = new DelayedSorter(getQualifiedInstanceName() + ".delayedSorter");
    this.gtEq0 = new Counter(getQualifiedInstanceName() + ".gtEq0");
    this.lt0 = new Counter(getQualifiedInstanceName() + ".lt0");
    
    this.iIn = this.delayedSorter.iIn;
    this.delayedSorter.gtEq0.connectTo(this.gtEq0.iIn);
    this.delayedSorter.lt0.connectTo(this.lt0.iIn);
  }
}
