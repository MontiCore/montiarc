/* (c) https://github.com/MontiCore/monticore */
package sim.comp;

import sim.automaton.ComponentState;
import sim.error.ISimulationErrorHandler;
import sim.sched.IScheduler;
import sim.serialiser.BackTrackHandler;

import java.util.List;

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
   * ID assigned by the scheduler.
   */
  private int id = -1;

  private BackTrackHandler bth;

  /**
   * Creates a new {@link AComponent}.
   */
  public AComponent() {
    super();
  }

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
   * @see ISimComponent#setComponentName(java.lang.String)
   */
  @Override
  public void setComponentName(String name) {
    this.componentName = name;
  }

  /**
   * @see IComponent#getComponentName()
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

  @Override
  public BackTrackHandler getBth() {
    return bth;
  }

  @Override
  public void setBth(BackTrackHandler bth) {
    this.bth = bth;
  }

  public ComponentState saveState(IComponent comp, List<ComponentState> cs) {
    if (cs.size() <= 1) {
      bth.saveComponentState(comp, cs.get(0));
      return cs.get(0);
    } else {
      return bth.handleUnderspecified(comp, cs);
    }
  }

}
