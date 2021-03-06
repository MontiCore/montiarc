/* (c) https://github.com/MontiCore/monticore */
package sim.sched;

import sim.comp.ISimComponent;
import sim.message.Message;
import sim.message.TickedMessage;
import sim.automaton.TransitionPath;
import sim.port.IForwardPort;
import sim.port.IInSimPort;
import sim.port.IOutSimPort;
import sim.port.IPortFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A slave of the {@link MasterScheduler}.
 */
public class SlaveScheduler implements IScheduler {

  /**
   * Flags true, if registered ports haven been checked.
   */
  private boolean isInitialized;

  /**
   * Tickless set for every component that is scheduled by this scheduler.
   */
  private final Map<ISimComponent, Set<IInSimPort<?>>> comp2tickless;

  /**
   * Maps a component to its incoming ports.
   */
  private final Map<ISimComponent, List<IInSimPort<?>>> comp2Ports;

  /**
   * Master scheduler of this slave.
   */
  private final MasterScheduler master;

  /**
   * Creates a new slave scheduler that is registered at the given master scheduler.
   *
   * @param master the master scheduler
   */
  protected SlaveScheduler(final MasterScheduler master) {
    this.master = master;
    comp2tickless = new HashMap<ISimComponent, Set<IInSimPort<?>>>();
    comp2Ports = new HashMap<ISimComponent, List<IInSimPort<?>>>();
    init();
  }

  /**
   * @see sim.port.IPortFactory#createInPort()
   */
  @Override
  public <T> IInSimPort<T> createInPort() {
    return master.createInPort();
  }

  /**
   * @see sim.port.IPortFactory#createOutPort()
   */
  @Override
  public <T> IOutSimPort<T> createOutPort() {
    return master.createOutPort();
  }

  /**
   * @see sim.port.IPortFactory#createForwardPort()
   */
  @Override
  public <T> IForwardPort<T> createForwardPort() {
    return master.createForwardPort();
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
    if (!master.isActive(port)) {
      master.setActive(port);
      if (msg.isTick()) {
        success = processTick(comp, port);
      } else {
        success = processData(comp, port, (Message<?>) msg);
      }
      master.setInactive(port);
    }

    return success;
  }

  /**
   * Removes unconnected ports.
   */
  private void checkRegisteredPorts() {
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
        comp2tickless.get(comp).removeAll(unconnected);
      }
    }

    isInitialized = true;
  }

  /**
   * @param comp component, where the tick occures
   * @param port port to schedule
   * @return true, if msg is handled by port's component immediately, else false
   */
  private boolean processTick(ISimComponent comp, IInSimPort<?> port) {
    Set<IInSimPort<?>> tickless = comp2tickless.get(comp);
    boolean success = tickless.remove(port);

    if (success && tickless.isEmpty()) {
      comp.handleTick();
      // reorganize tickless
      for (IInSimPort<?> p : comp2Ports.get(comp)) {
        p.wakeUp();
        if (!p.hasTickReceived()) {
          tickless.add(p);
        }
      }
      master.activate();
    } else {
      master.register(this, port);
      success = false;
    }

    return success;
  }

  /**
   * @param comp component, where the tick occures
   * @param port port to schedule
   * @param msg  msg to process
   * @return true, if msg is handled by port's component immediately, else false
   */
  private boolean processData(ISimComponent comp, IInSimPort<?> port, Message<?> msg) {
    boolean success = false;
    Set<IInSimPort<?>> tickless = comp2tickless.get(comp);
    if (tickless.contains(port)) {
      success = true;
      comp.handleMessage(port, msg);
      comp.checkConstraints();
    }
    return success;
  }

  /**
   * @see IScheduler#setupPort(IInSimPort)
   */
  @Override
  public void setupPort(IInSimPort<?> port) {
    ISimComponent component = port.getComponent();
    if (!comp2tickless.containsKey(component)) {
      comp2tickless.put(component, new HashSet<IInSimPort<?>>());
    }

    if (!comp2Ports.containsKey(component)) {
      comp2Ports.put(component, new ArrayList<IInSimPort<?>>());
    }
    comp2tickless.get(component).add(port);
    comp2Ports.get(component).add(port);

  }

  /**
   * @see IScheduler#init()
   */
  @Override
  public void init() {
    comp2tickless.clear();
    comp2Ports.clear();
  }

  /**
   * @see IScheduler#setPortFactory(sim.port.IPortFactory)
   */
  @Override
  public void setPortFactory(IPortFactory fact) {
    master.setPortFactory(fact);
  }

  /**
   * @see IScheduler#getPortFactory()
   */
  @Override
  public IPortFactory getPortFactory() {
    return master;
  }

  @Override
  public void handleSymbolic(Message<TransitionPath> msg, IInSimPort<?> port) {

  }

  @Override
  public Map<ISimComponent, List<IInSimPort<?>>> getComp2SimPorts() {
    return null;
  }

  @Override
  public void startScheduler(int time) {

  }
}
