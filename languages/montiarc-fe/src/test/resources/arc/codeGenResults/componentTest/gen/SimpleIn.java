package componentTest.gen;

public abstract class SimpleIn extends
    sim.generic.ASimpleInComponent<java.lang.String> implements
    componentTest.gen.interfaces.ISimpleIn {

  protected final componentTest.gen.helper.MessageFactory _messageFactory;

  protected sim.generic.IOutgoingPort<java.lang.Integer> int2;
  protected sim.generic.IOutgoingPort<java.lang.Integer> int1;

  public SimpleIn() {
    super();
    this._messageFactory = componentTest.gen.helper.MessageFactory.getInstance();
  }

  @Override
  public sim.generic.IOutgoingPort<java.lang.Integer> getInt2() {
    return this.int2;
  }

  @Override
  public sim.generic.IOutgoingPort<java.lang.Integer> getInt1() {
    return this.int1;
  }

  @Override
  public sim.generic.IIncomingPort<java.lang.String> getString() {
    return super.getPortIn();
  }

  @Override
  public void checkInvariants() throws sim.error.InvariantInjuredException {
  }

  @Override
  protected void sendTick() {
    sim.generic.Tick<java.lang.Integer> int2Tick = this._messageFactory
        .getTick("java.lang.Integer");
    getInt2().sendMessage(int2Tick);
    sim.generic.Tick<java.lang.Integer> int1Tick = this._messageFactory
        .getTick("java.lang.Integer");
    getInt1().sendMessage(int1Tick);
  }

  @Override
  public void setup(sim.IScheduler scheduler,
      sim.error.ISimulationErrorHandler errorHandler) {
    super.setup(scheduler, errorHandler);
    this.int2 = new sim.generic.Port<java.lang.Integer>(java.lang.Integer.class);
    this.int2.setComponent(this);
    this.int1 = new sim.generic.Port<java.lang.Integer>(java.lang.Integer.class);
    this.int1.setComponent(this);
  }
}