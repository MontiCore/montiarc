/* (c) https://github.com/MontiCore/monticore */
package sim.port;

import sim.automaton.TransitionPath;
import sim.comp.ISimComponent;
import sim.message.Message;
import sim.message.TickedMessage;
import sim.sched.IScheduler;

import java.io.Serializable;
import java.util.*;

/**
 * Default port implementation.
 *
 * @param <T> data type of this port
 */
public class Port<T> implements IPort<T>, Serializable {

  /**
   * Used to buffer messages.
   */
  private final Queue<TickedMessage<?>> bufferQueue;
  /**
   * A set of additional receivers. It is used if one outgoing port contains more then one receiver.
   */
  private final Set<IInPort<? super T>> receivers;
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
   * Flags, if this port is currently scheduled.
   */
  private boolean isInSchedule;

  /**
   * The scheduler of this port.
   */
  private IScheduler scheduler;

  /**
   * Default constructor.
   */
  public Port() {
    receivers = new LinkedHashSet<>();
    bufferQueue = new LinkedList<TickedMessage<?>>();
    isInSchedule = false;
  }

  /**
   * @see IPort#accept(T)
   */
  @Override
  public final void accept(T data) {
    accept(Message.of(data));
  }

  @Override
  //todo send to scheduler
  public void symbolicAccept(Message<TransitionPath> message) {
    scheduler.handleSymbolic(message, this);
  }

  /**
   * @see IPort#accept(T)
   */
  @Override
  public void accept(TickedMessage<? extends T> message) {
    if (!isInSchedule && !hasTickReceived() && scheduler != null) {
      isInSchedule = true;
      boolean success = scheduler.registerPort(this, message);
      if (!success) {
        bufferQueue.offer(message);
      }
      isInSchedule = false;
    } else {
      bufferQueue.offer(message);
    }
  }

  /**
   * @see IPort#addReceiver(IInPort)
   */
  @SuppressWarnings("unchecked")
  @Override
  public void addReceiver(IInPort<? super T> receiver) {
    this.receivers.add(receiver);
    if (!bufferQueue.isEmpty()) {
      List<TickedMessage<?>> copy = new ArrayList<TickedMessage<?>>(bufferQueue.size());
      copy.addAll(bufferQueue);
      for (TickedMessage<?> msg : copy) {
        receiver.accept((TickedMessage<T>) msg);
      }
    }
  }

  /**
   * @see IPort#getComponent()
   */
  @Override
  public final ISimComponent getComponent() {
    return component;
  }

  /**
   * @see IPort#getReceivers()
   */
  @Override
  public final Collection<IInPort<? super T>> getReceivers() {
    return receivers;
  }

  /**
   * @see IPort#hasTickReceived()
   */
  @Override
  public final boolean hasTickReceived() {
    TickedMessage<?> msg = bufferQueue.peek();
    return (msg != null && msg.isTick());
  }

  /**
   * @see IPort#hasUnprocessedMessages()
   */
  @Override
  public final boolean hasUnprocessedMessages() {
    return bufferQueue.size() > 0;
  }

  /**
   * @see sim.port.IInSimPort#isConnected()
   */
  @Override
  public boolean isConnected() {
    return isConnected;
  }

  /**
   * @see IPort#processBufferedMsgs()
   */
  @Override
  public final void processBufferedMsgs() {
    if (!hasTickReceived()) {
      TickedMessage<?> nextMsg = null;

      while ((nextMsg = bufferQueue.peek()) != null) {
        isInSchedule = true;
        boolean success = scheduler.registerPort(this, nextMsg);
        if (success) {
          bufferQueue.poll();
        } else {
          break;
        }
        isInSchedule = false;
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

  public void symbolicSend(Message<TransitionPath> m) {
    for (IInPort<? super T> receiver : receivers) {
      receiver.symbolicAccept(m);
    }
    // and accept the message (role incoming port)
    symbolicAccept(m);
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
    setComponent(component);
    this.scheduler = scheduler;
    if (getComponent() != null) {
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
  public final void wakeUp() {
    // remove blocking tick, if there is one
    if (hasTickReceived()) {
      bufferQueue.poll();
    }
  }

  protected IScheduler getScheduler() {
    return this.scheduler;
  }

  /**
   * @see sim.port.IInSimPort#setPortNumber(int)
   */
  @Override
  public final void setPortNumber(int nr) {
    this.number = nr;
  }

  /**
   * @see sim.port.IInSimPort#getPortNumber()
   */
  @Override
  public final int getPortNumber() {
    return number;
  }

}
