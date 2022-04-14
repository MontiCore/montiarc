/* (c) https://github.com/MontiCore/monticore */
package sim.port;

import sim.generic.ISimComponent;
import sim.generic.IStream;
import sim.generic.Message;
import sim.generic.Stream;
import sim.generic.TickedMessage;
import sim.generic.Transitionpath;

import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of an outgoing port.
 *
 * @param <T> data type of this port
 */
public class OutPort<T> implements IOutSimPort<T> {

  /**
   * Set of receiving incoming ports that are connected to this outgoing port.
   */
  protected Set<IInPort<? super T>> receivers;

  /**
   * Component that owns this port.
   */
  protected ISimComponent component;

  /**
   * Stream that is transmitted via this port.
   */
  protected IStream<T> stream;

  public OutPort() {
    receivers = new HashSet<IInPort<? super T>>();

    stream = new Stream<T>();
  }

  /**
   * @see IPort#addReceiver(sim.port.IInPort)
   */
  @Override
  public void addReceiver(IInPort<? super T> receiver) {
    this.receivers.add(receiver);

    for (TickedMessage<T> msg : stream.getHistory()) {
      receiver.accept(msg);
    }
  }

  /**
   * @see IPort#getComponent()
   */
  @Override
  public ISimComponent getComponent() {

    return this.component;
  }

  /**
   * @see IPort#getReceivers()
   */
  @Override
  public Set<IInPort<? super T>> getReceivers() {
    return receivers;
  }

  /**
   * @see IPort#send(TickedMessage)
   */
  @Override
  public void send(TickedMessage<T> message) {
    for (IInPort<? super T> rec : receivers) {
      rec.accept(message);
    }
    stream.add(message);
    stream.pollLastMessage();
  }

  @Override
  public void symbolicSend(Message<Transitionpath> message) {

  }

  /**
   * @see IPort#setComponent(sim.generic.ISimComponent)
   */
  @Override
  public void setComponent(ISimComponent component) {
    this.component = component;
  }
}
