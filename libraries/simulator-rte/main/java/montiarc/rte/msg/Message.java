/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.msg;

/**
 * Represents a concrete data message in the simulation framework. <br>
 *
 * @param <T> the type of the message's data
 */
public class Message<T> {

  protected final T data;

  public Message(final T data) {
    this.data = data;
  }

  public static <T> Message<T> of(T data) {
    return new Message<>(data);
  }

  public T getData() {
    return this.data;
  }

  @Override
  public String toString() {
    return this.getData() == null ? "null" : this.getData().toString();
  }

  @Override
  public boolean equals(Object o) {
    return (this == o || (o instanceof Message && (((Message<?>) o).getData() != null
      && ((Message<?>) o).getData().equals(this.getData())))
    );
  }

  @Override
  public int hashCode() {
    return this.getData() == null ? 0 : this.getData().hashCode();
  }
}
