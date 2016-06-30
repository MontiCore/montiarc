package sim.generic;

import de.se_rwth.commons.logging.Log;

/**
 * Represents a concrete data message in the simulation framework. <br>
 * <br>
 * Copyright (c) 2010 RWTH Aachen. All rights reserved.
 * 
 * @author Arne Haber
 * @version 13.10.2008
 * @param <T> Type of the data
 */
public class Message<T> extends TickedMessage<T> {
  
  /**
   * 
   */
  private static final long serialVersionUID = -6549666462012980055L;
  
  /** The data object. */
  protected final T data;
  
  /**
   * Creates a {@link Message} containing the given data.
   * 
   * @param data Message data
   */
  protected Message(final T data) {
    this.data = data;
  }
  
  /**
   * @param data data object, must not be null
   * @return creates a message that contains the given 'data' as data.
   * @throws NullPointerException if {@code data == null}
   */
  public static <T> Message<T> of(T data) {
    Log.errorIfNull(data, "0xMA001, Parameter data must not be null!");
    return new Message<T>(data);
  }
  
  @Override
  public boolean equals(Object o) {
    boolean equals = false;
    if (o instanceof Message) {
      Message<?> oMsg = (Message<?>) o;
      equals = oMsg.getData().equals(this.getData());
    }
    return equals;
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#hashCode() */
  @Override
  public int hashCode() {
    return getData().hashCode();
  }
  
  /**
   * @return the data from this TickedMessage
   */
  public final T getData() {
    return data;
  }
  
  @Override
  public String toString() {
    return this.data.toString();
  }
  
  /* (non-Javadoc)
   * @see sim.generic.TickedMessage#isTick() */
  @Override
  public boolean isTick() {
    return false;
  }
}
