/* (c) https://github.com/MontiCore/monticore */
package sim.error;

/**
 * Is thrown, if parts of the simulation are not implemented yet.
 */
public class NotImplementedYetException extends RuntimeException {

  /**
   * @param message exception message
   */
  public NotImplementedYetException(final String message) {
    super(message);
  }

  private static final long serialVersionUID = -2621245120182244300L;
}
