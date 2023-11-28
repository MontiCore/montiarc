/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.port;

import montiarc.rte.component.IComponent;
import montiarc.rte.msg.Message;
import montiarc.rte.msg.Tick;

import java.util.ArrayList;
import java.util.List;

/**
 * An outgoing port of a MontiArc component.
 * Connections between ports are implemented via the observer pattern.
 * In terms of the observer pattern, outgoing ports are observables for incoming ports.
 *
 * @param <T> the type that can be sent via this port
 */
public abstract class AbstractOutPort<T> extends AbstractBasePort<T> implements IOutPort<T> {

  /**
   * A list of all ports connected to this outgoing port.
   * <br>
   * Implemented as a list rather than a set because this allows for constant iteration (and thus message sending) order.
   */
  protected List<IInPort<? super T>> recipients = new ArrayList<>();

  protected AbstractOutPort(String qualifiedName, IComponent owner) {
    super(qualifiedName, owner);
  }

  /**
   * Connect the given incoming port to this outgoing port.
   *
   * @param recipient the port that should be connected to this port
   * @return true iff the given port is connected to this port when this method finishes
   */
  @Override
  public boolean connect(IInPort<? super T> recipient) {
    if (recipient == null) {
      return false;
    }

    if (!recipients.contains(recipient)) {
      recipients.add(recipient);
    }

    return true;
  }

  /**
   * Disconnect the given incoming port from this outgoing port.
   *
   * @param recipient the port that should be disconnected from this port
   * @return true iff the given port is not connected to this port when this method finishes
   */
  @Override
  public boolean disconnect(IInPort<? super T> recipient) {
    return recipients.remove(recipient) || !recipients.contains(recipient);
  }

  /**
   * Send out the given data as a message to all registered recipients,
   * if it is not filtered out by {@link #messageIsValidOnPort(Message)}.
   * <br>
   * Do not use this method to send ticks. Instead, use {@link #sendTick()}.
   * <br>
   * Dispatches to {@link #doSendMessage(T)} to allow for overriding in subclasses.
   *
   * @param data the data to send
   */
  @Override
  public void send(T data) {
    doSendMessage(data);
  }

  /**
   * Send out the given data as a message to all registered recipients,
   * if it is not filtered out by {@link #messageIsValidOnPort(Message)}.
   * <br>
   * Do not use this method to send ticks. Instead, use {@link #sendTick()}.
   *
   * @param data the data to send
   */
  protected void doSendMessage(T data) {
    doSendMessage(new Message<>(data));
  }


  /**
   * Send out the given message to all registered recipients,
   * if it is not filtered out by {@link #messageIsValidOnPort(Message)}.
   * <br>
   * Do not use this method to send ticks. Instead, use {@link #sendTick()}.
   * <br>
   * Dispatches to {@link #doSendMessage(Message)} to allow for overriding in subclasses.
   *
   * @param message the message to send
   */
  @Override
  public void send(Message<T> message) {
    doSendMessage(message);
  }

  /**
   * Send out the given message to all registered recipients,
   * if it is not filtered out by {@link #messageIsValidOnPort(Message)}.
   * <br>
   * Do not use this method to send ticks. Instead, use {@link #sendTick()}.
   *
   * @param message the message to send
   */
  protected void doSendMessage(Message<T> message) {
    if (messageIsValidOnPort(message)) {
      recipients.forEach(r -> r.receive(message));
    }
  }

  /**
   * Try to send a tick via this port.
   * Whether it is sent or not depends on {@link #messageIsValidOnPort(Message)}
   * <br>
   * Dispatches to {@link #doTick()} to allow for overriding in subclasses
   */
  @Override
  public void sendTick() {
    this.doTick();
  }

  /**
   * Try to send a tick via this port.
   * Whether it is sent or not depends on {@link #messageIsValidOnPort(Message)}
   * <br>
   * Uses {@link #doSendMessage(Message)} so that it sends a tick regardless of overrides.
   */
  protected void doTick() {
    doSendMessage(Tick.get());
  }
}
