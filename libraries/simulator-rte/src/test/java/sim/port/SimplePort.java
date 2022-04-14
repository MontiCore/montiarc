/* (c) https://github.com/MontiCore/monticore */
package sim.port;

import sim.IScheduler;
import sim.generic.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A Port that acts as an incoming and outgoing port. It contains a stream of type {@link IStream} to save and buffer
 * messages that are transmitted by this port.
 *
 * @param <T> data type of this port.
 */
public class SimplePort<T> extends AbstractPort<T> implements IPort<T> {

  /**
   * A set of additional receivers. It is used if one outgoing port contains more then one receiver.
   */
  protected List<IInPort<? super T>> receivers;

  /**
   * The incoming stream of this port.
   */
  protected IStream<T> stream;

  public SimplePort() {
    stream = new Stream<T>();
    receivers = new ArrayList<IInPort<? super T>>();
  }

  /**
   * @see IPort#accept(T)
   */
  @Override
  public void accept(T message) {
    accept(Message.of(message));
  }

  /**
   * @see IPort#accept(TickedMessage)
   */
  @SuppressWarnings("unchecked")
  @Override
  public void accept(TickedMessage<? extends T> message) {
    // add message to incoming stream
    getIncomingStream().add((TickedMessage<T>) message);
    // scheduler should control the activation from this port
    if (getScheduler() != null) {
      getScheduler().registerPort(this, message);
    }
  }

  @Override
  public void symbolicAccept(Message<Transitionpath> message) {

  }

  /**
   * @see IPort#addReceiver(IInPort)
   */
  @Override
  public void addReceiver(IInPort<? super T> receiver) {
    this.receivers.add(receiver);
  }

  /**
   * @return the incoming stream.
   */
  protected IStream<T> getIncomingStream() {
    return stream;
  }

  /**
   * @return the outgoing stream.
   */
  protected IStream<T> getOutgoingStream() {

    return stream;
  }

  /**
   * @see IPort#getReceivers()
   */
  @Override
  public Collection<IInPort<? super T>> getReceivers() {
    return receivers;
  }

  /**
   * @see IPort#hasTickReceived()
   */
  @Override
  public boolean hasTickReceived() {
    TickedMessage<? extends T> lastMsg = getIncomingStream().peekLastMessage();
    return lastMsg != null && lastMsg.isTick();
  }

  /**
   * @see IPort#hasUnprocessedMessages()
   */
  @Override
  public boolean hasUnprocessedMessages() {
    return getIncomingStream().getBuffer().size() > 0;
  }

  /**
   * @see IPort#processBufferedMsgs()
   */
  @Override
  public void processBufferedMsgs() {
    // not needed in a SimplePort
  }

  /**
   * @see IPort#send(TickedMessage)
   */
  @Override
  public void send(TickedMessage<T> message) {
    // send the given message to all additional receivers
    for (IInPort<? super T> receiver : getReceivers()) {
      receiver.accept(message);
    }
    // and accept the message (role incoming port)
    accept(message);
  }

  @Override
  public void symbolicSend(Message<Transitionpath> message) {

  }

  /**
   * @see IPort#setup(ISimComponent, IScheduler)
   */
  @Override
  public void setup(ISimComponent component, IScheduler scheduler) {
    setComponent(component);
    setScheduler(scheduler);
    if (component != null) {
      scheduler.setupPort(this);
    }
  }

  /**
   * @see IPort#wakeUp()
   */
  @Override
  public void wakeUp() {
    // not needed in a SimplePort
  }
}
