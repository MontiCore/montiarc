/* (c) https://github.com/MontiCore/monticore */
package sim.port;

import sim.message.IStream;
import sim.message.Stream;
import sim.message.TickedMessage;

/**
 * Forward port that additionally stores the transmitted stream.
 */
public class TestForwardPort<T> extends ForwardPort<T> {

  /**
   * Stores received messages.
   */
  private final IStream<T> stream;

  /**
   * Constructor for sim.port.TestForwardPort
   */
  public TestForwardPort() {
    stream = new Stream<T>();
  }

  @SuppressWarnings("unchecked")
  @Override
  public void accept(TickedMessage<? extends T> message) {
    super.accept(message);
    stream.add((TickedMessage<T>) message);
    stream.pollLastMessage();
  }
}
