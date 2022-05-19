/* (c) https://github.com/MontiCore/monticore */
package sim.dummys;

import sim.comp.AComponent;
import sim.comp.IComponent;
import sim.comp.ISimComponent;
import sim.comp.ITimedComponent;
import sim.error.ISimulationErrorHandler;
import sim.message.Message;
import sim.message.Tick;
import sim.message.TickedMessage;
import sim.port.IInPort;
import sim.port.IInSimPort;
import sim.port.TestPort;
import sim.sched.IScheduler;
import sim.serialiser.BackTrackHandler;

/**
 * Is used to directly control the test components time.
 */
public class ComponentTimeDummy extends AComponent implements ISimComponent, ITimedComponent {

  /**
   * Simulation error handler.
   */
  protected ISimulationErrorHandler handler;
  /**
   * Local time.
   */
  protected int time;
  private IInSimPort<String> in;
  private TestPort<String> out;

  /**
   * @see IComponent#checkConstraints()
   */
  @Override
  public void checkConstraints() {

  }

  @Override
  public ISimulationErrorHandler getErrorHandler() {
    return this.handler;
  }

  /**
   * @see ITimedComponent#getLocalTime()
   */
  @Override
  public int getLocalTime() {

    return time;
  }

  /**
   * @see IComponent#getComponentName()
   */
  @Override
  public String getComponentName() {
    return null;
  }

  @Override
  public void handleMessage(IInPort<?> port, Message<?> message) {
    out.send((TickedMessage<String>) message);
  }

  @Override
  public void handleTick() {
    out.send(Tick.get());
  }

  /**
   * @see AComponent#setComponentName(String)
   */
  @Override
  public void setComponentName(String name) {

  }

  /**
   * @param time the time to set
   */
  public void setTime(int time) {
    this.time = time;
  }

  @Override
  public void setup(IScheduler scheduler, ISimulationErrorHandler errorHandler, BackTrackHandler backTrackHandler) {

    this.handler = errorHandler;
    in = scheduler.createInPort();
    in.setup(this, scheduler);
    out = new TestPort<>();
    this.setBth(backTrackHandler);
  }

  public IInSimPort<String> getIn() {
    return in;
  }

  public TestPort<String> getOut() {
    return out;
  }
}
