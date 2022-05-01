/* (c) https://github.com/MontiCore/monticore */
package sim.port;


/**
 * Produces ports
 */
public interface IPortFactory {

  /**
   * Creates an incoming port.
   *
   * @param <T> type of the port
   * @return an incomin port
   */
  <T> IInSimPort<T> createInPort();

  /**
   * Creates an outgoing port.
   *
   * @param <T> type of the port
   * @return an outgoing port
   */
  <T> IOutSimPort<T> createOutPort();

  /**
   * Creates a forwarding port.
   *
   * @param <T> type of the port.
   * @return a forwarding port
   */
  <T> IForwardPort<T> createForwardPort();

}
