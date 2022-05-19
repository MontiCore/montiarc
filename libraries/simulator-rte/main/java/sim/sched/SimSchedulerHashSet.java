/* (c) https://github.com/MontiCore/monticore */
package sim.sched;

import sim.automaton.TransitionPath;
import sim.comp.ISimComponent;
import sim.message.Message;
import sim.message.TickedMessage;
import sim.port.*;

import java.util.*;

/**
 * Default simulation scheduler.
 */
public class SimSchedulerHashSet implements IScheduler {

  /**
   * Maps a component to its incoming ports.
   */
  private final Map<ISimComponent, List<IInSimPort<?>>> comp2Ports;

  /**
   * Set of tick-free ports (not received a tick yet) for every scheduled component.
   */
  private final Map<ISimComponent, Set<IInSimPort<?>>> comp2tickfree;

  /**
   * Flags true, if registered ports haven been checked.
   */
  private boolean isInitialized;

  /**
   * Used to produce ports.
   */
  private IPortFactory portFactory;

  /**
   * @deprecated Use {@link SchedulerFactory#createDefaultScheduler()} instead.
   */
  public SimSchedulerHashSet() {
    comp2tickfree = new HashMap<ISimComponent, Set<IInSimPort<?>>>();
    comp2Ports = new HashMap<ISimComponent, List<IInSimPort<?>>>();
    portFactory = new DefaultPortFactory();
    isInitialized = false;
    init();
  }

  /**
   * Removes unconnected ports.
   */
  protected void checkRegisteredPorts() {
    for (ISimComponent comp : comp2Ports.keySet()) {
      Set<IInSimPort<?>> unconnected = new HashSet<IInSimPort<?>>();
      List<IInSimPort<?>> portOfComp = comp2Ports.get(comp);

      for (IInSimPort<?> p : portOfComp) {
        if (!p.isConnected()) {
          unconnected.add(p);
        }
      }
      if (unconnected.size() != portOfComp.size()) {
        portOfComp.removeAll(unconnected);
        comp2tickfree.get(comp).removeAll(unconnected);
      }
    }
    isInitialized = true;

  }

  /**
   * @see IPortFactory#createForwardPort()
   */
  @Override
  public <T> IForwardPort<T> createForwardPort() {
    return portFactory.createForwardPort();
  }

  /**
   * @see IPortFactory#createInPort()
   */
  @Override
  public <T> IInSimPort<T> createInPort() {
    return portFactory.createInPort();
  }

  /**
   * @see sim.port.IPortFactory#createOutPort()
   */
  @Override
  public <T> IOutSimPort<T> createOutPort() {
    return portFactory.createOutPort();
  }

  /**
   * @return the comp2Ports
   */
  protected Map<ISimComponent, List<IInSimPort<?>>> getComp2Ports() {
    return comp2Ports;
  }

  /**
   * @return the comp2tickfree
   */
  protected Map<ISimComponent, Set<IInSimPort<?>>> getComp2tickfree() {
    return comp2tickfree;
  }

  /**
   * @see IScheduler#init()
   */
  @Override
  public void init() {
    comp2tickfree.clear();
    comp2Ports.clear();
  }

  /**
   * @see IScheduler#setupPort(IInSimPort)
   */
  @Override
  public void setupPort(IInSimPort<?> port) {
    ISimComponent component = port.getComponent();
    if (!comp2tickfree.containsKey(component)) {
      comp2tickfree.put(component, new HashSet<IInSimPort<?>>());
    }

    if (!comp2Ports.containsKey(component)) {
      comp2Ports.put(component, new ArrayList<IInSimPort<?>>());
    }
    comp2tickfree.get(component).add(port);
    comp2Ports.get(component).add(port);
  }

  /**
   * @param comp component of the given port
   * @param port port to schedule
   * @param msg  msg to process
   * @return true, if msg is handled by port's component immediately, else
   * false
   */
  protected boolean processData(ISimComponent comp, IInSimPort<?> port, Message<?> msg) {
    boolean success = false;
    Set<IInSimPort<?>> tickless = comp2tickfree.get(comp);
    if (tickless.contains(port)) {
      success = true;
      comp.handleMessage(port, msg);
      comp.checkConstraints();
    }

    return success;
  }

  /**
   * @param comp component of the given port
   * @param port port to schedule
   * @return true if the msg is handled immediately, else false
   */
  protected boolean processTick(ISimComponent comp, IInSimPort<?> port) {
    Set<IInSimPort<?>> tickfree = comp2tickfree.get(comp);

    boolean success = tickfree.remove(port);

    if (success && tickfree.isEmpty()) {
      List<IInSimPort<?>> allPorts = comp2Ports.get(comp);
      comp.handleTick();
      // wake ports up
      for (IInSimPort<?> p : allPorts) {
        p.wakeUp();
      }

      // reorganize tickfree ports
      for (IInSimPort<?> p : allPorts) {
        if (!p.hasTickReceived()) {
          tickfree.add(p);
        }
      }
      // trigger processing of buffered messages
      for (IInSimPort<?> p : allPorts) {
        p.processBufferedMsgs();
      }
    } else {
      success = false;
    }

    return success;
  }

  /**
   * @see IScheduler#registerPort(IInSimPort, TickedMessage)
   */
  @Override
  public boolean registerPort(IInSimPort<?> port, TickedMessage<?> msg) {
    if (!isInitialized) {
      checkRegisteredPorts();
    }
    boolean success = false;
    ISimComponent comp = port.getComponent();
    if (msg.isTick()) {
      success = processTick(comp, port);
    } else {
      success = processData(comp, port, (Message<?>) msg);
    }
    return success;
  }

  /**
   * @see IScheduler#setPortFactory(IPortFactory)
   */
  @Override
  public void setPortFactory(IPortFactory fact) {
    this.portFactory = fact;
  }

  /**
   * @see IScheduler#getPortFactory()
   */
  @Override
  public IPortFactory getPortFactory() {
    return this.portFactory;
  }

  @Override
  public void handleSymbolic(Message<TransitionPath> msg, IInSimPort<?> port) {

  }
}
