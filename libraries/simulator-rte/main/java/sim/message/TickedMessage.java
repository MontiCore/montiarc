/* (c) https://github.com/MontiCore/monticore */
package sim.message;

import java.io.Serializable;

/**
 * Abstract carrier data type that is used for messages in the simulation framework.
 *
 * @param <T> data type from this message
 */
public abstract class TickedMessage<T> implements Serializable {

  /**
   *
   */
  private static final long serialVersionUID = 6109585129447746214L;

  /**
   * Checks, if this message is either a Tick or a concrete Message.
   *
   * @return true, if this message is a Tick.
   */
  public abstract boolean isTick();
}
