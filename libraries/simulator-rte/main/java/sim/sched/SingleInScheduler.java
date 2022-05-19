/* (c) https://github.com/MontiCore/monticore */
package sim.sched;

import sim.automaton.TransitionPath;
import sim.comp.ISimComponent;
import sim.error.NotImplementedYetException;
import sim.message.Message;
import sim.message.TickedMessage;
import sim.port.*;

/**
 * Scheduler optimized for components with one incoming port.
 */
class SingleInScheduler implements IScheduler {

  /**
   * The port factory to use.
   */
  private IPortFactory portFactory;

  /**
   * Flags, if this scheduler is active.
   */
  private boolean isActive;

  protected SingleInScheduler() {
    init();
    portFactory = new DefaultPortFactory();
    isActive = false;
  }

  /**
   * @see sim.port.IPortFactory#createInPort()
   */
  @Override
  public <T> IInSimPort<T> createInPort() {
    throw new NotImplementedYetException("A Single in Scheduler is attended to be used for a SingleInComponent. " +
        "As this component is its port itself, this method should never be called.");
  }

  /**
   * @see sim.port.IPortFactory#createOutPort()
   */
  @Override
  public <T> IOutSimPort<T> createOutPort() {
    return portFactory.createOutPort();
  }

  /**
   * @see sim.port.IPortFactory#createForwardPort()
   */
  @Override
  public <T> IForwardPort<T> createForwardPort() {
    throw new NotImplementedYetException("A Single in Scheduler is attended to be used for a SingleInComponent. " +
        "As this component is its port itself, this method should never be called.");
  }

  /**
   * @see IScheduler#registerPort(IInSimPort, TickedMessage)
   */
  @Override
  public boolean registerPort(IInSimPort<?> port, TickedMessage<?> msg) {
    boolean success = false;
    ISimComponent comp = port.getComponent();

    if (!isActive) {
      isActive = true;
      if (msg.isTick()) {
        comp.handleTick();
        port.wakeUp();
      } else {
        comp.handleMessage(port, (Message<?>) msg);
        comp.checkConstraints();
      }
      success = true;
      isActive = false;
    }

    return success;
  }

  /**
   * @see IScheduler#setupPort(IInSimPort)
   */
  @Override
  public void setupPort(IInSimPort<?> port) {
  }

  /**
   * @see IScheduler#init()
   */
  @Override
  public void init() {

  }

  /**
   * @see IScheduler#setPortFactory(sim.port.IPortFactory)
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
