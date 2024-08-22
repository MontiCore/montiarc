/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.behavior;

/**
 *
 * @param <T> Sync message type. Its objects conclude the values of the input ports at the time of a tick.
 */
public interface Behavior<T> {

  /**
   * Initialize behavior.
   * If no initialization is necessary implement an empty body.
   */
  void init();

  /**
   * Trigger the behavior reaction to a tick.
   * @param msg The message concluding the values of the input ports in case of synchronised behavior.
   *            For event behavior, null shall be passed. Event behavior must not process this parameter.<br>
   */
  /* The design decision of unifying the event and synchronised tick method is due to the 150% design of
   * variable components that store a reference to their behavior via a subclass of this class.
   * Therefore, references to synchronous and event behavior must share a common type with signature for
   * both event and synchronous ticks - defined by this method.
   */
  void tick(T msg);
}
