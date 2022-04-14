/* (c) https://github.com/MontiCore/monticore */
package sim.dummys;

import sim.IScheduler;
import sim.error.ISimulationErrorHandler;
import sim.generic.AComponent;
import sim.generic.IComponent;
import sim.generic.ISimComponent;
import sim.generic.ITimedComponent;
import sim.generic.Message;
import sim.port.IInPort;

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

  /**
   * @see sim.generic.IComponent#checkConstraints()
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

  }

  @Override
  public void handleTick() {

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

  /**
   * @see sim.generic.IComponent#setup(IScheduler, ISimulationErrorHandler)
   */
  @Override
  public void setup(IScheduler scheduler, ISimulationErrorHandler errorHandler) {
    this.handler = errorHandler;
  }
}
