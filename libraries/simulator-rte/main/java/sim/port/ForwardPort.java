/* (c) https://github.com/MontiCore/monticore */
package sim.port;

import sim.automaton.TransitionPath;
import sim.comp.ISimComponent;
import sim.message.Message;
import sim.message.TickedMessage;
import sim.sched.IScheduler;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Is used to encapsulate the forwarded ports from inner components in one
 * architecture component port.
 *
 * @param <T> data type of this port
 */
public class ForwardPort<T> implements IForwardPort<T> {

  /**
   * Contains the inner forwarded ports.
   */
  private final Set<IInSimPort<? super T>> innerPorts;
  private int number = -1;
  /**
   * The component owning the encapsulating port.
   */
  private ISimComponent component;

  /**
   *
   */
  public ForwardPort() {
    innerPorts = new LinkedHashSet<>();
  }

  /**
   * @see sim.port.IInPort#accept(Object) (java.lang.Object)
   */
  @Override
  public void accept(T message) {
    accept(Message.of(message));
  }

  /**
   * @see sim.port.IPort#accept(TickedMessage)
   */
  @Override
  public void accept(TickedMessage<? extends T> message) {
    for (IInPort<? super T> p : this.getInnerPorts()) {
      p.accept(message);
    }
  }

  @Override
  public void symbolicAccept(Message<TransitionPath> message) {
    for (IInPort<? super T> p : this.getInnerPorts()) {
      p.symbolicAccept(message);
    }
  }

  @Override
  public void add(IInSimPort<? super T> port) {
    if (!this.getInnerPorts().contains(port)) {
      this.getInnerPorts().add(port);
    }
  }

  @Override
  public void addReceiver(IInPort<? super T> receiver) {
    if (receiver instanceof IInSimPort) {
      add((IInSimPort<? super T>) receiver);
    }
  }

  @Override
  public ISimComponent getComponent() {
    return this.component;
  }

  /**
   * @return the first encapsulated port. This port is representative, because
   * other encapsulated ports contain the same stream. Returns null, if no port
   * is registered.
   */
  private IInSimPort<? super T> getFirstPort() {
    IInSimPort<? super T> firstPort;
    if (!getInnerPorts().isEmpty()) {
      firstPort = getInnerPorts().iterator().next();
    } else {
      firstPort = null;
    }
    return firstPort;
  }

  /**
   * @return a set containing the inner forwarded ports.
   */
  private Set<IInSimPort<? super T>> getInnerPorts() {
    return this.innerPorts;
  }

  @Override
  public Set<IInPort<? super T>> getReceivers() {
    Set<IInPort<? super T>> copy = new HashSet<IInPort<? super T>>();
    for (IInSimPort<? super T> p : getInnerPorts()) {
      copy.add(p);
    }
    return copy;
  }

  @Override
  public boolean hasTickReceived() {
    IInSimPort<? super T> fp = getFirstPort();
    if (fp != null) {
      return getFirstPort().hasTickReceived();
    } else {
      return false;
    }
  }

  @Override
  public boolean hasUnprocessedMessages() {
    IInSimPort<? super T> fp = getFirstPort();
    if (fp != null) {
      return getFirstPort().hasUnprocessedMessages();
    } else {
      return false;
    }
  }

  /**
   * @see IPort#isConnected()
   */
  @Override
  public boolean isConnected() {
    IInSimPort<? super T> fp = getFirstPort();
    if (fp != null) {
      return getFirstPort().isConnected();
    } else {
      return false;
    }
  }

  /**
   * @see IPort#processBufferedMsgs()
   */
  @Override
  public void processBufferedMsgs() {
    for (IInSimPort<? super T> p : getInnerPorts()) {
      p.processBufferedMsgs();
    }
  }

  @Override
  public boolean removeEncapsulatedPort(IInSimPort<? super T> port) {
    return this.getInnerPorts().remove(port);
  }

  @Override
  public void send(TickedMessage<T> message) {
    accept(message);
  }

  @Override
  public void symbolicSend(Message<TransitionPath> message) {
    symbolicAccept(message);
  }

  @Override
  public void setComponent(ISimComponent component) {
    this.component = component;
  }

  /**
   * @see IPort#setConnected()
   */
  @Override
  public void setConnected() {
    for (IInSimPort<? super T> p : getInnerPorts()) {
      p.setConnected();
    }
  }

  @Override
  public void setup(ISimComponent component, IScheduler scheduler) {
    setComponent(component);
    for (IInSimPort<? super T> p : this.getInnerPorts()) {
      p.setup(component, scheduler);
    }
  }

  @Override
  public void wakeUp() {
    for (IInSimPort<? super T> p : getInnerPorts()) {
      p.wakeUp();
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

}
