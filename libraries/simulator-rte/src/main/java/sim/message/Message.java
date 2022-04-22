/* (c) https://github.com/MontiCore/monticore */
package sim.message;

/**
 * Represents a concrete data message in the simulation framework.
 *
 * @param <T> Type of the data
 */
public class Message<T> extends TickedMessage<T> {

  private static final long serialVersionUID = -6549666462012980055L;

  /**
   * Symbolic Tag for symbolic execution
   */
  private boolean isSymbolic = false;

  /**
   * The data object.
   */
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
    //Log.errorIfNull(data, "", Parameter data must not be null!");
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

  /**
   * @see java.lang.Object#hashCode()
   */
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

  public void setSymbolic(boolean symbolic) {
    isSymbolic = symbolic;
  }

  public boolean getSymbolic() {
    return isSymbolic;
  }

  @Override
  public String toString() {
    return this.data.toString();
  }

  /**
   * @see TickedMessage#isTick()
   */
  @Override
  public boolean isTick() {
    return false;
  }
}
