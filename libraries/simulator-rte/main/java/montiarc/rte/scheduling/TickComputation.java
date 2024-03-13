/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.scheduling;

import montiarc.rte.component.IComponent;
import montiarc.rte.component.ITimedComponent;

public class TickComputation implements IComputation {

  protected final ITimedComponent owner;

  public TickComputation(ITimedComponent owner) {
    this.owner = owner;
  }

  @Override
  public void run() {
    owner.handleTick();
  }

  @Override
  public IComponent getOwner() {
    return owner;
  }
}
