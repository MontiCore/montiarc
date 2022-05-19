/* (c) https://github.com/MontiCore/monticore */
package sim.comp;


import sim.error.ISimulationErrorHandler;
import sim.sched.IScheduler;
import sim.serialiser.BackTrackHandler;

/**
 * Abstract implementation of a timed component.
 */
public abstract class ATimedComponent extends AComponent implements ITimedComponent {

  /**
   * The local time of this component.
   */
  private int localTime;

  public ATimedComponent() {
    super();
    localTime = 0;
  }

  /**
   * @see ITimedComponent#getLocalTime()
   */
  @Override
  public int getLocalTime() {
    return localTime;
  }

  /**
   * Increments the components time by one.
   */
  protected final void incLocalTime() {
    localTime += 1;
  }

  /**
   * This method is called at the start of a new time slice and may be used to realize time triggered behavior.
   */
  protected abstract void timeStep();

  public abstract void setup(IScheduler s, ISimulationErrorHandler eh, BackTrackHandler backTrackHandler);
}
