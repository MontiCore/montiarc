/* (c) https://github.com/MontiCore/monticore */
package sim.port;

import sim.automaton.TransitionPath;
import sim.comp.ISimComponent;
import sim.message.Message;
import sim.message.TickedMessage;
import sim.sched.IScheduler;

import java.util.*;

/**
 * A port that is to be used with a master/slave scheduler.
 *
 * @param <T> data type of this port
 */
public class MasterSlavePort<T> implements IPort<T> {

  /**
   * To buffer messages.
   */
  private final Queue<TickedMessage<?>> bufferQueue;
  /**
   * A set of additional receivers. It is used if one outgoing port contains more then one receiver.
   */
  private final List<IInPort<? super T>> receivers;
  /**
   * Port number.
   */
  private int number = -1;
  /**
   * The component owning this port.
   */
  private ISimComponent component;
  /**
   * Flags, if this port is connected.
   */
  private boolean isConnected;
  /**
   * The scheduler of this port.
   */
  private IScheduler scheduler;

  /**
   * Default constructor.
   */
  protected MasterSlavePort() {
    receivers = new ArrayList<IInPort<? super T>>();
    bufferQueue = new LinkedList<TickedMessage<?>>();
  }

  /**
   * @see IPort#accept(T)
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
    if (!hasTickReceived() && scheduler != null) {
      boolean success = scheduler.registerPort(this, message);
      if (!success) {
        bufferQueue.offer(message);
      }
    } else {
      bufferQueue.offer(message);
    }
  }

  @Override
  public void symbolicAccept(Message<TransitionPath> message) {

  }

  /**
   * @see IPort#addReceiver(IInPort)
   */
  @Override
  public void addReceiver(IInPort<? super T> receiver) {
    this.receivers.add(receiver);
  }

  /**
   * @see IPort#getComponent()
   */
  @Override
  public ISimComponent getComponent() {
    return component;
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
    TickedMessage<?> msg = bufferQueue.peek();
    return (msg != null && msg.isTick());
  }

  /**
   * @see IPort#hasUnprocessedMessages()
   */
  @Override
  public boolean hasUnprocessedMessages() {
    return bufferQueue.size() > 0;
  }

  /**
   * @see IPort#isConnected()
   */
  @Override
  public boolean isConnected() {
    return isConnected;
  }

  /**
   * @see IPort#processBufferedMsgs()
   */
  @Override
  public void processBufferedMsgs() {
    TickedMessage<?> nextMsg = null;

    while ((nextMsg = bufferQueue.peek()) != null) {
      boolean success = scheduler.registerPort(this, nextMsg);
      if (success) {
        bufferQueue.poll();
      } else {
        break;
      }
    }
  }

  /**
   * @see IPort#send(TickedMessage)
   */
  @Override
  public void send(TickedMessage<T> message) {
    // send the given message to all additional receivers
    for (IInPort<? super T> receiver : receivers) {
      receiver.accept(message);
    }
    // and accept the message (role incoming port)
    accept(message);
  }

  @Override
  public void symbolicSend(Message<TransitionPath> message) {

  }

  /**
   * @see IPort#setComponent(ISimComponent)
   */
  @Override
  public void setComponent(ISimComponent component) {
    this.component = component;

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
    this.component = component;
    this.scheduler = scheduler;
    if (component != null) {
      scheduler.setupPort(this);
    }
  }

  @Override
  public String toString() {
    return hashCode() + ": " + hasTickReceived() + " -> " + bufferQueue.toString();
  }

  /**
   * @see IPort#wakeUp()
   */
  @Override
  public void wakeUp() {
    if (hasTickReceived()) {
      // remove blocking tick
      bufferQueue.poll();
    }
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

  @Override
  public void processMessageQueue() {

  }

  @Override
  public Queue<TickedMessage<?>> getMessageQueue() {
    return null;
  }

  @Override
  public void setMessageQueue(Queue<TickedMessage<?>> messageQueue) {

  }
}
