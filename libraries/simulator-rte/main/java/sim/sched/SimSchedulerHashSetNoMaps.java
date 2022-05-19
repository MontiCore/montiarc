/* (c) https://github.com/MontiCore/monticore */
package sim.sched;

import sim.automaton.TransitionPath;
import sim.comp.ISimComponent;
import sim.message.Message;
import sim.message.TickedMessage;
import sim.port.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Default simulation scheduler w/o maps.
 */
public class SimSchedulerHashSetNoMaps implements IScheduler {

  /**
   * Maps a component to its incoming ports.
   */
  private final List<List<IInSimPort<?>>> comp2SimPorts;

  /**
   * Set of tick-free ports (not received a tick yet) for every scheduled component.
   */
  private final List<Set<IInSimPort<?>>> comp2tickfree;

  /**
   * Flags true, if a component (identified by its position) is initialized correctly.
   */
  private final List<Boolean> isInitialized;

  /**
   * Used to produce ports.
   */
  private IPortFactory portFactory;

  public SimSchedulerHashSetNoMaps() {
    comp2tickfree = new ArrayList<Set<IInSimPort<?>>>();
    comp2SimPorts = new ArrayList<List<IInSimPort<?>>>();
    portFactory = new DefaultPortFactory();
    isInitialized = new ArrayList<Boolean>();
    init();
  }

  /**
   * Removes unconnected ports from the given components scheduler and marks the component as initialized.
   *
   * @param comp the component to initialize
   */
  private void checkComponentInit(ISimComponent comp) {
    if (!isInitialized.get(comp.getSimulationId())) {
      int compId = comp.getSimulationId();
      Set<IInSimPort<?>> unconnected = new HashSet<IInSimPort<?>>();
      List<IInSimPort<?>> portOfComp = comp2SimPorts.get(compId);

      for (IInSimPort<?> p : portOfComp) {
        if (!p.isConnected()) {
          unconnected.add(p);
        }
      }
      if (unconnected.size() != portOfComp.size()) {
        portOfComp.removeAll(unconnected);
        comp2tickfree.get(compId).removeAll(unconnected);
      }
      isInitialized.set(compId, true);
    }
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
   * @return the comp2SimPorts
   */
  protected List<List<IInSimPort<?>>> getComp2Ports() {
    return comp2SimPorts;
  }

  /**
   * @return the comp2tickfree
   */
  protected List<Set<IInSimPort<?>>> getComp2tickfree() {
    return comp2tickfree;
  }

  /**
   * @see IScheduler#init()
   */
  @Override
  public void init() {
    comp2tickfree.clear();
    comp2SimPorts.clear();
  }

  /**
   * @see IScheduler#setupPort(IInSimPort)
   */
  @Override
  public void setupPort(IInSimPort<?> port) {
    ISimComponent component = port.getComponent();
    if (component.getSimulationId() < 0) {
      component.setSimulationId(comp2SimPorts.size());
      comp2tickfree.add(new HashSet<IInSimPort<?>>());
      comp2SimPorts.add(new ArrayList<IInSimPort<?>>());
      isInitialized.add(false);
    }
    int id = component.getSimulationId();
    comp2tickfree.get(id).add(port);
    comp2SimPorts.get(id).add(port);
  }

  /**
   * @param comp component of the given port
   * @param port port to schedule
   * @param msg  msg to process
   * @return true, if msg is handled by port's component immediately, else false
   */
  protected boolean processData(ISimComponent comp, IInSimPort<?> port, Message<?> msg) {
    boolean success = false;
    Set<IInSimPort<?>> tickless = comp2tickfree.get(comp.getSimulationId());
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
   * @return true, if msg is handled by port's component immediately, else false
   */
  protected boolean processTick(ISimComponent comp, IInSimPort<?> port) {
    int id = comp.getSimulationId();
    Set<IInSimPort<?>> tickfree = comp2tickfree.get(id);

    boolean success = tickfree.remove(port);

    if (success && tickfree.isEmpty()) {
      List<IInSimPort<?>> allPorts = comp2SimPorts.get(id);
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
    boolean success = false;
    ISimComponent comp = port.getComponent();
    checkComponentInit(comp);

    if (msg.isTick()) {
      success = processTick(comp, port);
    } else {
      success = processData(comp, port, (Message<?>) msg);
    }

    return success;
  }

  /**
   * @see IScheduler#setPortFactors(sim.port.IPortFactory)
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
