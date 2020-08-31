/* (c) https://github.com/MontiCore/monticore */
package factory.warehouse.transporter;

import de.montiarc.runtimes.timesync.implementation.IComputable;

public class ContainerImpl implements IComputable<ContainerInput, ContainerResult> {
  
  private int capacity;
  
  public ContainerImpl(int capacity) {
    this.capacity = capacity;
  }
  
  @Override
  public ContainerResult getInitialValues() {
    return new ContainerResult(null, capacity);
  }
  
  @Override
  public ContainerResult compute(ContainerInput input) {
    return new ContainerResult(null, capacity);
  }
}