/* (c) https://github.com/MontiCore/monticore */
package sim.comp;

import sim.error.ISimulationErrorHandler;
import sim.message.IStream;
import sim.message.Message;
import sim.message.TickedMessage;
import sim.port.IInPort;
import sim.port.IPort;
import sim.port.ITestPort;
import sim.port.Port;
import sim.sched.IScheduler;
import sim.sched.SchedulerFactory;
import sim.serialiser.BackTrackHandler;

/**
 * Is used for components with just one incoming port and two or more outgoing ports.
 *
 * @param <Tin>
 */
public abstract class ASingleIn<Tin> extends Port<Tin>
    implements ITestPort<Tin>, SimpleInPortInterface<Tin>, ISimComponent {

  /**
   * ID assigned by the scheduler.
   */
  private int id = -1;
  /**
   * Used to store a test port, if we are in test mode.
   */
  private IPort<Tin> testPort;
  /**
   * Handles ArcSimProblemReports.
   */
  private ISimulationErrorHandler errorHandler;
  /**
   * Name of this component.
   */
  private String componentName;

  /**
   * Default constructor.
   */
  public ASingleIn() {
    super();
  }

  /**
   * @see sim.port.ITestPort#getStream()
   */
  @Override
  public IStream<Tin> getStream() {
    if (testPort != null && testPort instanceof ITestPort) {
      return ((ITestPort<Tin>) testPort).getStream();
    } else {
      return null;
    }
  }

  /**
   * @return the errorHandler
   */
  @Override
  public ISimulationErrorHandler getErrorHandler() {
    return errorHandler;
  }

  /**
   * @see ISimComponent#getComponentName()
   */
  @Override
  public String getComponentName() {
    return this.componentName;
  }

  /**
   * @return the portIn
   */
  public IInPort<Tin> getPortIn() {
    return this;
  }

  /**
   * @see sim.port.Port#accept(TickedMessage)
   */
  @Override
  public void accept(TickedMessage<? extends Tin> message) {
    super.accept(message);
    if (testPort != null) {
      testPort.accept(message);
    }
  }

  /**
   * @see ISimComponent#handleMessage(IInPort, Message)
   */
  @Override
  @SuppressWarnings("unchecked")
  public void handleMessage(IInPort<?> port, Message<?> message) {
    messageReceived((Tin) message.getData());
  }

  /**
   * @param errorHandler the errorHandler to set
   */
  protected void setErrorHandler(ISimulationErrorHandler errorHandler) {
    this.errorHandler = errorHandler;
  }

  /**
   * @see ISimComponent#getComponentName()
   */
  @Override
  public void setComponentName(String name) {
    this.componentName = name;
  }

  /**
   * @see Port#setup(ISimComponent, IScheduler) 
   */
  @Override
  public void setup(IScheduler scheduler, sim.error.ISimulationErrorHandler errorHandler, BackTrackHandler backTrackHandler) {
    IScheduler localSched = SchedulerFactory.createSingleInScheduler();
    localSched.setupPort(this);
    localSched.setPortFactory(scheduler.getPortFactory());
    super.setup(this, localSched);
    setErrorHandler(errorHandler);

    IInPort<Tin> tp = scheduler.createInPort();
    if (!(tp.getClass().getName().equals(Port.class.getName()))) {
      this.testPort = (IPort<Tin>) tp;
    }
  }

  /**
   * @return the scheduler
   */
  protected IScheduler getScheduler() {
    return super.getScheduler();
  }

  /**
   * @param scheduler the scheduler to set
   */
  protected void setScheduler(IScheduler scheduler) {
    // ignored
  }

  /**
   * @see ISimComponent#setSimulationId(int)
   */
  @Override
  public final void setSimulationId(int id) {
    this.id = id;
  }

  /**
   * @see ISimComponent#getSimulationId()
   */
  @Override
  public final int getSimulationId() {
    return this.id;
  }
}
