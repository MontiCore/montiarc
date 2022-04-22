/* (c) https://github.com/MontiCore/monticore */
package sim.port;

import sim.sched.IScheduler;
import sim.comp.ISimComponent;
import sim.message.Message;
import sim.message.TickedMessage;
import sim.Automaton.Transitionpath;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Stores all accepted messages in a buffer. They may be accessed using the poll method.
 *
 * @param <T> data type of the port
 */
public class MessageStoringPort<T> implements IPort<T> {

  /**
   * Port number.
   */
  private int number = -1;

  /**
   * Queues all received messages.
   */
  private final Queue<T> buffer;

  /**
   * Flags, if this port is connected.
   */
  protected boolean isConnected;

  public MessageStoringPort() {
    buffer = new LinkedList<T>();
  }

  /**
   * @see IPort#accept(java.lang.Object)
   */
  @Override
  public void accept(T data) {
    accept(Message.of(data));

  }

  /**
   * @see IPort#accept(TickedMessage)
   */
  @Override
  public void accept(TickedMessage<? extends T> message) {
    if (message instanceof Message) {
      @SuppressWarnings("unchecked")
      Message<T> casted = (Message<T>) message;
      buffer.offer(casted.getData());
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


  }

  /**
   * @see IPort#getComponent()
   */
  @Override
  public ISimComponent getComponent() {

    return null;
  }

  /**
   * @see IPort#getReceivers()
   */
  @Override
  public Collection<IInPort<? super T>> getReceivers() {

    return null;
  }

  /**
   * @see IPort#hasTickReceived()
   */
  @Override
  public boolean hasTickReceived() {

    return false;
  }

  /**
   * @see IPort#hasUnprocessedMessages()
   */
  @Override
  public boolean hasUnprocessedMessages() {

    return false;
  }

  /**
   * @see IPort#isConnected()
   */
  @Override
  public boolean isConnected() {
    return isConnected;
  }

  /**
   * Retrieves and removes the head of the message buffer and returns it.
   *
   * @return the head of the message buffer, or null if the buffer is empty
   */
  public T pollMessage() {
    return buffer.poll();
  }

  /**
   * @see IPort#processBufferedMsgs()
   */
  @Override
  public void processBufferedMsgs() {

  }

  /**
   * @see IPort#send(TickedMessage)
   */
  @Override
  public void send(TickedMessage<T> message) {
    accept(message);

  }

  @Override
  public void symbolicSend(Message<Transitionpath> message) {

  }

  /**
   * @see IPort#setComponent(ISimComponent)
   */
  @Override
  public void setComponent(ISimComponent component) {

  }

  /**
   * @see IPort#setConnected()
   */
  @Override
  public void setConnected() {
    isConnected = true;
  }

  /**
   * @see IPort#setup(ISimComponent, IScheduler)
   */
  @Override
  public void setup(ISimComponent component, IScheduler scheduler) {

  }

  /**
   * @see IPort#wakeUp()
   */
  @Override
  public void wakeUp() {

  }

  /**
   * @see sim.port.IInSimPort#setPortNumber(int)
   */
  @Override
  public void setPortNumber(int nr) {
    this.number = nr;
  }

  /**
   * @see sim.port.IInSimPort#getPortNumber()
   */
  @Override
  public int getPortNumber() {
    return number;
  }
}
