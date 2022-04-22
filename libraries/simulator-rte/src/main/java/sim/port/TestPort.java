/* (c) https://github.com/MontiCore/monticore */
package sim.port;

import sim.message.IStream;
import sim.message.Stream;
import sim.message.TickedMessage;

/**
 * Can be used in Tests as an outgoing port. Received messages are stored in the field stream.
 *
 * @param <T> type of the port
 */
public class TestPort<T> extends Port<T> implements ITestPort<T> {

  /**
   * Stores received messages.
   */
  private final IStream<T> stream;

  public TestPort() {
    super();
    stream = new Stream<T>();
  }

  /**
   * @see IPort#accept(TickedMessage)
   */
  @SuppressWarnings("unchecked")
  @Override
  public void accept(TickedMessage<? extends T> message) {
    super.accept(message);
    stream.add((TickedMessage<T>) message);
    stream.pollLastMessage();
  }

  /**
   * @see sim.port.ITestPort#getStream()
   */
  @SuppressWarnings("unchecked")
  @Override
  public IStream<T> getStream() {
    IStream<T> s = stream;
    if (!getReceivers().isEmpty()) {
      IInPort<? super T> rec = getReceivers().iterator().next();
      if (rec instanceof ITestPort) {
        s = ((ITestPort<T>) rec).getStream();
      }
    }
    return s;
  }
}
