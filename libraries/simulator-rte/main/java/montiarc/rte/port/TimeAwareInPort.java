/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.port;

import montiarc.rte.component.IComponent;
import montiarc.rte.msg.PassAllMessageFilter;
import montiarc.rte.msg.Tick;

import java.util.Objects;

/**
 * An incoming port of a MontiArc component that can receive ticks.
 *
 * @param <T> the type that can be received via this port
 */
public class TimeAwareInPort<T> extends AbstractInPort<T> implements ITimeAwareInPort<T>, PassAllMessageFilter<T> {

  public TimeAwareInPort(String qualifiedName, IComponent<?, ?> owner) {
    super(qualifiedName, owner);
  }

  /**
   * Whether this port is currently blocked by a tick, i.e., it is not tickfree (see Haber Diss p.96).
   *
   * @return true if the next buffered message is a tick
   */
  @Override
  public boolean isTickBlocked() {
    return Objects.equals(this.peekBuffer(), Tick.get());
  }

  /**
   * Remove the next buffered message if it is a tick
   */
  @Override
  public void dropBlockingTick() {
    if (this.isTickBlocked()) {
      this.pollBuffer();
    }
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
