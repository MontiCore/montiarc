/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.port;

import montiarc.rte.component.IComponent;
import montiarc.rte.scheduling.Scheduler;

public class TickPort extends TimeAwarePortForComposition<Object> {
  @Override
  protected void processReceivedData(Object data) {
    throw new UnsupportedOperationException("TickPort does not support receiving data.");
  }
  
  public TickPort(IComponent owner, Scheduler scheduler) {
    super(".#tick", owner, scheduler);
  }
}
