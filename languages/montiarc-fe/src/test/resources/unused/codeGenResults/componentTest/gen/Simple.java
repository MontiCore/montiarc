package componentTest.gen;

public abstract class Simple extends
    sim.generic.ASimpleComponent<java.lang.String, java.lang.Integer> implements
    componentTest.gen.interfaces.ISimple {

  public Simple() {
    super();
  }

  @Override
  public sim.generic.IIncomingPort<java.lang.String> getString() {
    return super.getPortIn();
  }

  @Override
  public sim.generic.IOutgoingPort<java.lang.Integer> getInteger() {
    return super.getPortOut();
  }

  /*
   * (non-Javadoc)
   * 
   * @see sim.generic.IComponent#checkInvariants()
   */
  @Override
  public void checkInvariants() throws sim.error.InvariantInjuredException {
  }

  /*
   * (non-Javadoc)
   * 
   * @see sim.generic.IComponent\#setPortScheduler(sim.IScheduler)
   */
  @Override
  public void setup(sim.IScheduler scheduler,
      sim.error.ISimulationErrorHandler errorHandler) {
    super.setup(scheduler, errorHandler);
  }
}