/* (c) https://github.com/MontiCore/monticore */
package sim.port;

import sim.sched.IScheduler;
import sim.comp.ISimComponent;
import sim.message.Message;
import sim.message.TickedMessage;
import sim.Automaton.Transitionpath;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Scheduler for {@link SimplePort}s.
 * It also checks constraints after each handleMessage call on components.
 */
public class SimulationScheduler implements IScheduler {

  /**
   * Port factory to use.
   */
  protected IPortFactory portFactory;
  /**
   * Maps components to ports.
   */
  protected Map<ISimComponent, Set<IInSimPort<?>>> components2Ports;
  /**
   * Maps components to ports without ticks.
   */
  protected Map<ISimComponent, Set<IInSimPort<?>>> components2PortsWithoutTick;

  public SimulationScheduler() {
    init();
    portFactory = new SimplePortFactory();
  }

  /**
   * @see IScheduler#registerPort(IInSimPort, TickedMessage)
   */
  @Override
  public boolean registerPort(IInSimPort<?> port, TickedMessage<?> lastMessage) {
    // is the last message a tick?
    if (lastMessage.isTick()) {
      processTick(port);
    }
    // is it a message that is not blocked by a previously received tick
    else {
      processData(port);
    }
    return true;
  }

  /**
   * Process data on the given port.
   *
   * @param port port that received a data message
   */
  private void processData(IInSimPort<?> port) {
    ISimComponent comp = port.getComponent();
    if (components2PortsWithoutTick.get(comp).contains(port)) {
      // remove message from buffer
      Message<?> lastMessage = (Message<?>) ((SimplePort<?>) port).getIncomingStream().pollLastMessage();

      // let component handle the message
      comp.handleMessage(port, lastMessage);


      comp.checkConstraints();

      // process next message
      TickedMessage<?> nextMessage = ((SimplePort<?>) port).getIncomingStream().peekLastMessage();
      if (nextMessage != null) {
        registerPort(port, nextMessage);
      }
    }
  }

  /**
   * @param port the port on which the tick arrived
   */
  private void processTick(IInSimPort<?> port) {
    ISimComponent comp = port.getComponent();
    Set<IInSimPort<?>> portsWithoutTicks = components2PortsWithoutTick.get(comp);

    // remove the port from the portWithoutTick set, if last message is not
    // a tick
    // (which must not be the case when a feedback-loop exist)
    // if we do not have success, this port has received a blocking tick
    // before
    boolean success = false;
    TickedMessage<?> lastMsg = ((SimplePort<?>) port).getIncomingStream().peekLastMessage();
    if (lastMsg.isTick()) {
      success = portsWithoutTicks.remove(port);
    }

    // all ports have a tick received?
    if (success && portsWithoutTicks.isEmpty()) {
      for (IInSimPort<?> currentPort : components2Ports.get(comp)) {
        // remove ticks from ports' buffers
        ((SimplePort<?>) currentPort).getIncomingStream().pollLastMessage();

        // next message is not a tick
        if (!currentPort.hasTickReceived()) {
          portsWithoutTicks.add(currentPort);
        }
      }
      // send tick on component.
      comp.handleTick();

      // check, if more messages are to be processed
      for (IInSimPort<?> currentPort : components2Ports.get(comp)) {
        TickedMessage<?> nextMessage = ((SimplePort<?>) currentPort).getIncomingStream().peekLastMessage();
        if (nextMessage != null) {
          this.registerPort(currentPort, nextMessage);
        }
      }
    }
  }

  /**
   * @see IScheduler#init()
   */
  @Override
  public void init() {
    this.components2Ports = new HashMap<ISimComponent, Set<IInSimPort<?>>>();
    this.components2PortsWithoutTick = new HashMap<ISimComponent, Set<IInSimPort<?>>>();
  }

  /**
   * @see IScheduler#setupPort(IInSimPort)
   */
  @Override
  public void setupPort(IInSimPort<?> port) {
    ISimComponent component = port.getComponent();

    if (!components2PortsWithoutTick.containsKey(component)) {
      components2PortsWithoutTick.put(component, new HashSet<IInSimPort<?>>());
    }

    if (!components2Ports.containsKey(component)) {
      components2Ports.put(component, new HashSet<IInSimPort<?>>());
    }
    components2PortsWithoutTick.get(component).add(port);
    components2Ports.get(component).add(port);
  }

  /**
   * @see IPortFactory#createInPort()
   */
  @Override
  public <T> IInSimPort<T> createInPort() {
    return portFactory.createInPort();
  }

  /**
   * @see IPortFactory#createForwardPort()
   */
  @Override
  public <T> IForwardPort<T> createForwardPort() {
    return portFactory.createForwardPort();
  }

  /**
   * @see sim.port.IPortFactory#createOutPort()
   */
  @Override
  public <T> IOutSimPort<T> createOutPort() {
    return portFactory.createOutPort();
  }

  /**
   * @see IScheduler#getPortFactory()
   */
  @Override
  public IPortFactory getPortFactory() {
    return portFactory;
  }

  /**
   * @see IScheduler#setPortFactory(sim.port.IPortFactory)
   */
  @Override
  public void setPortFactory(IPortFactory fact) {

  }

  @Override
  public void handleSymbolic(Message<Transitionpath> msg, IInSimPort<?> port) {

  }
}
