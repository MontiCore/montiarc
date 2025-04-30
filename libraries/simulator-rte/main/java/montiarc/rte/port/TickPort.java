/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.port;

import de.se_rwth.commons.logging.Log;
import montiarc.rte.component.Component;
import montiarc.rte.logging.Aspects;
import montiarc.rte.logging.DataFormatter;
import montiarc.rte.msg.Tick;
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
    super(owner.getName() + ".>tick", owner, scheduler);
  }

  @Override
  public void sendTick() {
    // Overwrite to Log.debug instead of info
    Log.debug(() -> DataFormatter.TK, this.getQualifiedName() + "#" + Aspects.SEND_MSG);
    this.send(Tick.get());
  }
}
