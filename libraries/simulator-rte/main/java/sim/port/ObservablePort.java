/* (c) https://github.com/MontiCore/monticore */
package sim.port;

import sim.automaton.TransitionPath;
import sim.comp.ISimComponent;
import sim.message.Message;
import sim.message.TickedMessage;
import sim.sched.IScheduler;

import java.util.Collection;
import java.util.Observable;
import java.util.Queue;

/**
 * A port that notifies its observers if data messages are accepted.
 *
 * @param <T>
 */
public class ObservablePort<T> extends Observable implements IPort<T> {

  /**
   * Flags, if this port is connected.
   */
  protected boolean isConnected;
  /**
   * Port number.
   */
  private int number = -1;

  /**
   * @see IPort#accept(java.lang.Object)
   */
  @Override
  public void accept(T data) {
    setChanged();
    notifyObservers(data);
    clearChanged();
  }

  /**
   * @see IPort#accept(TickedMessage)
   */
  @Override
  public void accept(TickedMessage<? extends T> message) {
    if (!message.isTick()) {
      @SuppressWarnings("unchecked")
      Message<T> msg = (Message<T>) message;
      accept(msg.getData());
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
  public void symbolicSend(Message<TransitionPath> message) {

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
