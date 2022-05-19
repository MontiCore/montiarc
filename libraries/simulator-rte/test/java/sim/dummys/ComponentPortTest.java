/* (c) https://github.com/MontiCore/monticore */
package sim.dummys;


import sim.comp.AComponent;
import sim.comp.ISimComponent;
import sim.comp.ITimedComponent;
import sim.error.ISimulationErrorHandler;
import sim.message.Message;
import sim.port.IInPort;
import sim.sched.IScheduler;
import sim.serialiser.BackTrackHandler;

/**
 * Is used to directly control the test components time.
 */
public class ComponentPortTest extends AComponent implements ISimComponent, ITimedComponent {

  /**
   * Simulation error handler.
   */
  protected ISimulationErrorHandler handler;

  /**
   * Local time.
   */
  protected int time;

  /**
   *
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
   *
   */

  @Override
  public void handleMessage(IInPort<?> port, Message<?> message) {

  }

  @Override
  public void handleTick() {

  }

  /**
   * @param time the time to set
   */
  public void setTime(int time) {
    this.time = time;
  }

  /**
   *
   */
  @Override
  public void setup(IScheduler scheduler, ISimulationErrorHandler errorHandler, BackTrackHandler backTrackHandler) {
    this.handler = errorHandler;
  }
}
