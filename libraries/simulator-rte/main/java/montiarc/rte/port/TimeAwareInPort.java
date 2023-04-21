/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.port;

import montiarc.rte.msg.Message;
import montiarc.rte.msg.PassAllMessageFilter;
import montiarc.rte.msg.Tick;

import java.util.Objects;

/**
 * An incoming port of a MontiArc component that can receive ticks.
 *
 * @param <T> the type that can be received via this port
 */
public abstract class TimeAwareInPort<T> extends AbstractInPort<T> implements PassAllMessageFilter<T> {

  public TimeAwareInPort(String qualifiedName) {
    super(qualifiedName);
  }

  /**
   * Whether this port is currently blocked by a tick, i.e., it is not tickfree (see Haber Diss p.96).
   *
   * @return true if the next buffered message is a tick
   */
  public boolean isTickBlocked() {
    return Objects.equals(this.peekBuffer(), Tick.get());
  }

  /**
   * Remove the next buffered message if it is a tick
   */
  public void dropBlockingTick() {
    if (this.isTickBlocked()) {
      this.pollBuffer();
    }
  }
}
