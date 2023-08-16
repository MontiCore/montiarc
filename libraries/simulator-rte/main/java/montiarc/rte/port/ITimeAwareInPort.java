/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.port;

public interface ITimeAwareInPort<T> extends IInPort<T> {
  
  /**
   * Whether this port is currently blocked by a tick, i.e., it is not tickfree (see Haber Diss p.96).
   *
   * @return true if the next buffered message is a tick
   */
  boolean isTickBlocked();
  
  /**
   * Remove the next buffered message if it is a tick
   */
  void dropBlockingTick();
  
  /**
   * Continue consuming messages that may have been queued
   * because a tick could not be dropped.
   */
  void continueAfterDroppedTick();
}
