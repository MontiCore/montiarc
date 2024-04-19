/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.port;

public interface SyncAwareInPort<T> extends ITimeAwareInPort<T> {

  /**
   * Drop all messages except for the last one that is queued before the next tick.
   * If there is no tick queued, all messages except for the last one are dropped.
   */
  void dropMessagesIgnoredBySync();
}
