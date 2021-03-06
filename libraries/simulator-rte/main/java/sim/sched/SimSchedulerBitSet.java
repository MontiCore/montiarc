/* (c) https://github.com/MontiCore/monticore */
package sim.sched;

import sim.automaton.TransitionPath;
import sim.comp.ISimComponent;
import sim.message.Message;
import sim.message.TickedMessage;
import sim.port.*;

import java.util.*;

/**
 * Default simulation scheduler for MontiArc simulations.
 */
class SimSchedulerBitSet implements IScheduler {

  private final Map<ISimComponent, List<IInSimPort<?>>> comp2SimPorts;

  /**
   * Set of tick-free ports (not received a tick yet) for every scheduled component.
   */
  private final Map<ISimComponent, BitSet> comp2TickFree;

  /**
   * Flags true, if all components are initialized correctly. Is reset, if a new component is registered via {@link
   * #setupPort(IInSimPort)}
   */
  private boolean allInitialized;

  /**
   * Used to produce ports.
   */
  private IPortFactory portFactory;

  protected SimSchedulerBitSet() {
    comp2TickFree = new HashMap<ISimComponent, BitSet>();
    comp2SimPorts = new HashMap<ISimComponent, List<IInSimPort<?>>>();
    portFactory = new DefaultPortFactory();
    allInitialized = false;
    init();
  }

  /**
   * Organizes {@link #comp2TickFree} for all registered components.
   */
  private void checkComponentInit() {
    if (!allInitialized) {
      for (ISimComponent comp : comp2SimPorts.keySet()) {
        BitSet tickless = comp2TickFree.get(comp);
        if (tickless == null) {
          Set<IInSimPort<?>> unconnected = new HashSet<IInSimPort<?>>();

          List<IInSimPort<?>> portOfComp = comp2SimPorts.get(comp);

          for (IInSimPort<?> p : portOfComp) {
            if (!p.isConnected()) {
              unconnected.add(p);
            }
          }
          if (unconnected.size() != portOfComp.size()) {
            portOfComp.removeAll(unconnected);
          }
          int size = portOfComp.size();
          tickless = new BitSet(size);
          for (int i = 0; i < size; i++) {
            portOfComp.get(i).setPortNumber(i);
            tickless.set(i, true);
          }
        }
        comp2TickFree.put(comp, tickless);
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
  public Map<ISimComponent, List<IInSimPort<?>>> getComp2SimPorts() {
    return this.comp2SimPorts;
  }

  @Override
  public void startScheduler(int time) {

  }

  /**
   * @return comp2tickfree
   */
  protected Map<ISimComponent, BitSet> getComp2TickFree() {
    return this.comp2TickFree;
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

  /**
   * @see IScheduler#init()
   */
  @Override
  public void init() {
    comp2TickFree.clear();
  }

  /**
   * @see IScheduler#setupPort(IInSimPort)
   */
  @Override
  public void setupPort(IInSimPort<?> port) {
    ISimComponent component = port.getComponent();
    if (!comp2SimPorts.containsKey(component)) {
      component.setSimulationId(comp2SimPorts.size());
      comp2SimPorts.put(component, new LinkedList<IInSimPort<?>>());
      // we added a component that has not been initialized yet
      allInitialized = false;
    }
    comp2SimPorts.get(component).add(port);
  }

  /**
   * @param comp component of the given port
   * @param port port to schedule
   * @param msg  msg to process
   * @return true, if msg is handled by port's component immediately, else false
   */
  protected boolean processData(ISimComponent comp, IInSimPort<?> port, Message<?> msg) {
    boolean success = false;
    BitSet tickless = comp2TickFree.get(comp);

    if (tickless.get(port.getPortNumber())) {
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
    BitSet tickfree = comp2TickFree.get(comp);

    boolean success = false;
    tickfreeRemove(tickfree, port);

    if (tickfree.isEmpty()) {
      success = true;
      List<IInSimPort<?>> allPorts = comp2SimPorts.get(comp);
      comp.handleTick();
      // wake ports up
      for (IInSimPort<?> p : allPorts) {
        p.wakeUp();
      }

      // reorganize tickfree ports
      for (IInSimPort<?> p : allPorts) {
        if (!p.hasTickReceived()) {
          tickfree.set(p.getPortNumber());
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
   * @see IScheduler#registerPort(IInSimPort, TickedMessage)
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
   * @see IScheduler#setPortFactory(IPortFactory)
   */
  @Override
  public void setPortFactory(IPortFactory fact) {
    this.portFactory = fact;
  }

  /**
   * Checks, if the given port is tickfree now. If so, it is set to be blocked by a tick.
   *
   * @param tickfree tickfree storage
   * @param port     port to check
   * @return true, if the given port has not been blocked by a tick before.
   */
  protected void tickfreeRemove(BitSet tickfree, IInSimPort<?> port) {
    int nr = port.getPortNumber();
    if (tickfree.get(nr)) {
      tickfree.clear(nr);
    }
  }
}
