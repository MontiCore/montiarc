/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.port;

import montiarc.rte.port.messages.PassAllMessageFilter;
import montiarc.rte.port.messages.Message;

import java.util.Objects;

/**
 * An incoming port of a MontiArc component that can receive ticks.
 *
 * @param <DataType> the type that can be received via this port
 */
public abstract class TimeAwareInPort<DataType> extends AbstractInPort<DataType>
    implements PassAllMessageFilter<DataType> {
  
  public TimeAwareInPort(String qualifiedName) {
    super(qualifiedName);
  }
  
  /**
   * Whether this port is currently blocked by a tick, i.e., it is not tickfree (see Haber Diss p.96).
   *
   * @return true if the next buffered message is a {@link Message#tick tick}
   */
  public boolean isTickBlocked() {
    return Objects.equals(this.peekBuffer(), Message.tick);
  }
  
  /**
   * Remove the next buffered message if it is a {@link Message#tick tick}
   */
  public void dropBlockingTick() {
    if(this.isTickBlocked()) {
      this.pollBuffer();
    }
  }
}