/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.port.messages;

import java.util.Objects;

/**
 * A wrapper class used for messages sent across ports.
 * Having this wrapper class makes is easier to identify ticks and
 * allows for wrapping {@code null} messages which might otherwise cause problems (e.g., in buffering empty messages).
 *
 * @param <T> the type of the message held by the object
 */
public class Message<T> {

  protected final T value;

  public Message(T value) {
    this.value = value;
  }

  public T getValue() {
    return value;
  }

  public static final Message tick = new Message(Tick.tick);

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || this.getClass() != o.getClass()) {
      return false;
    }

    Message<?> other = (Message<?>) o;

    return Objects.equals(this.value, other.value);
  }

  @Override
  public int hashCode() {
    return value == null ? 0 : value.hashCode();
  }

  @Override
  public String toString() {
    return "Message{" +
      "value=" + value +
      '}';
  }

  /**
   * A type without methods or fields.
   * Its only purpose is to represent the
   * tick that delimits time slices in MontiArc.
   */
  protected static class Tick {

    protected static Tick tick = new Tick();

    private Tick() {}

    @Override
    public String toString() {
      return "Tick";
    }
  }
}
