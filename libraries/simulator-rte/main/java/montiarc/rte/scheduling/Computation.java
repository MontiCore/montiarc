/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.scheduling;

import montiarc.rte.component.IComponent;

/**
 * A data class grouping a computation with its owning component.
 * Intended for schedulers which decide computation order based on registering component rather than on registration order.
 */
public class Computation {
  
  protected final IComponent owner;
  protected final Runnable computation;
  
  public Computation(IComponent owner, Runnable computation) {
    this.owner = owner;
    this.computation = computation;
  }
  
  public void run() {
    this.computation.run();
  }
  
  public IComponent getOwner() {
    return owner;
  }
}
