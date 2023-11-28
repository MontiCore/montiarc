/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.port;

import montiarc.rte.component.IComponent;
import montiarc.rte.msg.PassAllMessageFilter;

/**
 * An incoming port of a MontiArc component that can receive ticks.
 *
 * @param <T> the type that can be received via this port
 */
public class TimeAwareInPort<T> extends AbstractInPort<T> implements ITimeAwareInPort<T>, PassAllMessageFilter<T> {

  public TimeAwareInPort(String qualifiedName, IComponent owner) {
    super(qualifiedName, owner);
  }
  
  /**
   * Continue consuming messages that may have been queued
   * because a tick could not be dropped.
   */
  @Override
  public void continueAfterDroppedTick() {
    while(!isBufferEmpty() && !isTickBlocked()) {
      handleBuffer();
    }
  }
}
