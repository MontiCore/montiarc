/* (c) https://github.com/MontiCore/monticore */
package sim.sched;

import sim.IScheduler;
import sim.generic.ISimComponent;
import sim.generic.Message;
import sim.generic.TickedMessage;
import sim.generic.Transitionpath;
import sim.port.DefaultPortFactory;
import sim.port.IForwardPort;
import sim.port.IInSimPort;
import sim.port.IOutSimPort;
import sim.port.IPortFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Default simulation scheduler for MontiArc simulations.
 */
class SimSchedulerPortMapNoMaps implements IScheduler {

  private final List<List<IInSimPort<?>>> comp2SimPorts;

  /**
   * Set of tick-free ports (not received a tick yet) for every scheduled component.
   */
  private final List<IPortMap> comp2tickfree;

  private int symbolicID = 1;

  /**
   * Flags true, if all components are initialized correctly. Is reset, if a new component is registered
   */
  private boolean allInitialized;

  /**
   * Used to produce ports.
   */
  private IPortFactory portFactory;

  /**
   *
   */
  protected SimSchedulerPortMapNoMaps() {
    comp2tickfree = new ArrayList<IPortMap>();
    comp2SimPorts = new ArrayList<List<IInSimPort<?>>>();
    portFactory = new DefaultPortFactory();
    allInitialized = false;
    init();
  }


  /**
   * Organizes {@link #comp2tickfree} for all registered components.
   */
  private void checkComponentInit() {
    if (!allInitialized) {
      for (int compId = 0; compId < comp2SimPorts.size(); compId++) {
        if (comp2tickfree.get(compId) == null) {
          Set<IInSimPort<?>> unconnected = new HashSet<IInSimPort<?>>();
          List<IInSimPort<?>> portOfComp = comp2SimPorts.get(compId);

          for (IInSimPort<?> p : portOfComp) {
            if (!p.isConnected()) {
              unconnected.add(p);
            }
          }
          if (unconnected.size() != portOfComp.size()) {
            portOfComp.removeAll(unconnected);
          }
          comp2tickfree.set(compId, new PortMap(portOfComp));
        }
      }
      allInitialized = true;
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
   * @return comp2SimPorts
   */
  protected List<List<IInSimPort<?>>> getComp2SimPorts() {
    return this.comp2SimPorts;
  }

  /**
   * @return comp2tickfree
   */
  protected List<IPortMap> getComp2tickfree() {
    return this.comp2tickfree;
  }

  /**
   * @see sim.IScheduler#getPortFactory()
   */
  @Override
  public IPortFactory getPortFactory() {
    return this.portFactory;
  }

  /**
   * @see sim.IScheduler#init()
   */
  @Override
  public void init() {
    comp2tickfree.clear();
  }

  /**
   * @see sim.IScheduler#setupPort(IInSimPort)
   */
  @Override
  public void setupPort(IInSimPort<?> port) {
    ISimComponent component = port.getComponent();
    if (component.getSimulationId() < 0) {
      component.setSimulationId(comp2SimPorts.size());
      comp2SimPorts.add(new LinkedList<IInSimPort<?>>());
      comp2tickfree.add(null);
      // we added a component that has not been initialized yet
      allInitialized = false;
    }
    comp2SimPorts.get(component.getSimulationId()).add(port);
  }

  /**
   * @param comp component of the given port
   * @param port port to schedule
   * @param msg  msg to process
   * @return true, if msg is handled by port's component immediately, else false
   */
  protected boolean processData(ISimComponent comp, IInSimPort<?> port, Message<?> msg) {
    boolean success = false;
    IPortMap tickless = comp2tickfree.get(comp.getSimulationId());

    if (tickless.isTickFree(port)) {
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
    IPortMap tickfree = comp2tickfree.get(id);

    boolean success = false;
    tickfree.setPortBlocked(port);

    if (tickfree.allPortsBlocked()) {
      success = true;
      List<IInSimPort<?>> allPorts = comp2SimPorts.get(id);
      comp.handleTick();
      // wake ports up
      for (IInSimPort<?> p : allPorts) {
        p.wakeUp();
      }

      // reorganize tickfree ports
      for (IInSimPort<?> p : allPorts) {
        if (!p.hasTickReceived()) {
          tickfree.setPortTickfree(p);
        }
      }
      // trigger processing of buffered messages
      for (IInSimPort<?> p : allPorts) {
        p.processBufferedMsgs();
      }
    }

    return success;
  }

  /**
   * @see sim.IScheduler#registerPort(IInSimPort, TickedMessage)
   */
  @Override
  public boolean registerPort(IInSimPort<?> port, TickedMessage<?> msg) {
    checkComponentInit();
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
   * @see sim.IScheduler#setupPort(IInSimPort)
   */
  @Override
  public void setPortFactory(IPortFactory fact) {
    this.portFactory = fact;
  }

  @Override
  //todo symbolic queue
  public void handleSymbolic(Message<Transitionpath> msg, IInSimPort<?> port) {
    Transitionpath tp = msg.getData();

    ISimComponent comp = port.getComponent();

    comp.handleMessage(port, msg);

    if (tp.getLast().getId() == symbolicID) {
      //todo go along path and reset symbolicID
    } else {
      symbolicID = tp.getLast().getId();
    }
  }
}


