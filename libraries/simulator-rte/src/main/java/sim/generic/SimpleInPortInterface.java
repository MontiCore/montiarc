/* (c) https://github.com/MontiCore/monticore */
package sim.generic;

/**
 * Is used as the port interface for {@link ASingleIn}.
 *
 * @param <Tin> data type of the single in port
 */
public interface SimpleInPortInterface<Tin> {

  /**
   * Is called, if a message is received on the incoming port.
   *
   * @param message the received message
   */
  void messageReceived(Tin message);
}
