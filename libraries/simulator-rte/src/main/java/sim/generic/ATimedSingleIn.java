/* (c) https://github.com/MontiCore/monticore */
package sim.generic;

/**
 * Abstract implementation for timed components with one incoming port.
 */
public abstract class ATimedSingleIn<Tin> extends ASingleIn<Tin> implements ITimedComponent {

  /**
   * The local time of this component.
   */
  private int localTime;

  /**
   * Default constructor.
   */
  public ATimedSingleIn() {
    super();
    localTime = 0;
  }

  /**
   * @see sim.generic.ITimedComponent#getLocalTime()
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
}
