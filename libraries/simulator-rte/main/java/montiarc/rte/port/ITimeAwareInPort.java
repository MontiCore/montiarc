/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.port;

import montiarc.rte.msg.Tick;

import java.util.Objects;

public interface ITimeAwareInPort<T> extends IInPort<T> {
  
  /**
   * Whether this port is currently blocked by a tick, i.e., it is not tickfree (see Haber Diss p.96).
   *
   * @return true if the next buffered message is a tick
   */
  default boolean isTickBlocked() {
    return Objects.equals(this.peekBuffer(), Tick.get());
  }
  
  /**
   * Remove the next buffered message if it is a tick
   */
  default void dropBlockingTick() {
    if (this.isTickBlocked()) {
      this.pollBuffer();
    }
  }
}
