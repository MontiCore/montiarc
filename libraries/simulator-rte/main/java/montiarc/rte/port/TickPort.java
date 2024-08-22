/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.port;

import montiarc.rte.component.Component;
import montiarc.rte.scheduling.Scheduler;

/**
 * Simulator internal port that synchronizes the execution of components during scheduling.
 * This port is not an element of the underlying component model.
 */
public class TickPort extends ScheduledPort<NoMsgType> {
  @Override
  protected void processReceivedData(NoMsgType data) {
    throw new UnsupportedOperationException("TickPort does not support receiving data.");
  }
  
  public TickPort(Component owner, Scheduler scheduler) {
    super(".#tick", owner, scheduler);
  }
}
