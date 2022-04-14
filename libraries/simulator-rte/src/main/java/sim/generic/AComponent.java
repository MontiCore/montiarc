/* (c) https://github.com/MontiCore/monticore */
package sim.generic;

import sim.IScheduler;
import sim.error.ISimulationErrorHandler;

/**
 * Represents an abstract component in the simulation
 */
public abstract class AComponent implements ISimComponent {

  /**
   * Handles ArcSimProblemReports.
   */
  private ISimulationErrorHandler errorHandler;

  /**
   * Name of this component.
   */
  private String componentName;

  /**
   * Scheduler of this component.
   */
  private IScheduler scheduler;

  /**
   * Id assigned by the scheduler.
   */
  private int id = -1;

  /**
   * @return the scheduler
   */
  protected IScheduler getScheduler() {
    return scheduler;
  }

  /**
   * @param scheduler the scheduler to set
   */
  protected void setScheduler(IScheduler scheduler) {
    this.scheduler = scheduler;
  }

  /**
   * Creates a new {@link AComponent}.
   */
  public AComponent() {
    super();
  }

  /**
   * @return the errorHandler
   */
  @Override
  public ISimulationErrorHandler getErrorHandler() {
    return errorHandler;
  }

  /**
   * @param errorHandler the errorHandler to set
   */
  protected void setErrorHandler(ISimulationErrorHandler errorHandler) {
    this.errorHandler = errorHandler;
  }

  /**
   * @see sim.generic.ISimComponent#setComponentName(java.lang.String)
   */
  @Override
  public void setComponentName(String name) {
    this.componentName = name;
  }

  /**
   * @see sim.generic.IComponent#getComponentName()
   */
  @Override
  public String getComponentName() {
    return this.componentName;
  }

  @Override
  public String toString() {
    if (getComponentName() != null) {
      return getComponentName();
    } else {
      return this.getClass().getName();
    }
  }

  /**
   * @see sim.generic.ISimComponent#setSimulationId(int)
   */
  @Override
  public final void setSimulationId(int id) {
    this.id = id;
  }

  /**
   * @see sim.generic.ISimComponent#getSimulationId()
   */
  @Override
  public final int getSimulationId() {
    return this.id;
  }
}
