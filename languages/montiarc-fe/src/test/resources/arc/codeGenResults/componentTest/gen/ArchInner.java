package componentTest.gen;

public class ArchInner extends sim.generic.AComponent implements
    componentTest.gen.interfaces.IArchInner {

  protected componentTest.gen.interfaces.IComplex complex;

  protected componentTest.gen.interfaces.IMerge<java.lang.Integer> merge;

  protected componentTest.gen.interfaces.ISimpleIn simpleIn;

  public ArchInner() {
    super();
  }

  @Override
  public java.util.Set<sim.generic.IIncomingPort<java.lang.String>> getString() {
    java.util.Set<sim.generic.IIncomingPort<java.lang.String>> returnSet = new java.util.HashSet<sim.generic.IIncomingPort<java.lang.String>>();
    returnSet.add(simpleIn.getString());
    returnSet.add(complex.getStr2());
    returnSet.add(complex.getStr1());
    return returnSet;
  }

  @Override
  public sim.generic.IOutgoingPort<java.lang.Integer> getIntOut2() {
    return merge.getOutput2();
  }

  @Override
  public sim.generic.IOutgoingPort<java.lang.Integer> getIntOut1() {
    return merge.getOutput1();
  }

  @Override
  public int getLocalTime() {
    return getIntOut2().getComponent().getLocalTime();
  }

  @Override
  public void setup(sim.IScheduler scheduler, sim.error.ISimulationErrorHandler errorHandler) {
    this.complex = componentTest.gen.factories.ComplexFactory.getInstance()
        .create();
    this.complex.setName("Complex complex");
    this.complex.setup(scheduler, errorHandler);
    this.merge = componentTest.gen.factories.MergeFactory.getInstance().create(
        "ISO-8859-1", java.lang.Integer.class);
    this.merge.setName("Merge merge");
    this.merge.setup(scheduler, errorHandler);
    this.simpleIn = componentTest.gen.factories.SimpleInFactory.getInstance()
        .create();
    this.simpleIn.setName("SimpleIn simpleIn");
    this.simpleIn.setup(scheduler, errorHandler);

    //   connect complex.int1 -> merge.input3;
    this.complex.getInt1().addReceiver(merge.getInput3());

    //   connect complex.int2 -> merge.input4;
    this.complex.getInt2().addReceiver(merge.getInput4());

    //   connect simpleIn.int2 -> merge.input2;
    this.simpleIn.getInt2().addReceiver(merge.getInput2());

    //   connect simpleIn.int1 -> merge.input1;
    this.simpleIn.getInt1().addReceiver(merge.getInput1());
  }

  @Override
  protected boolean areTicksReceivedOnAllPorts() {
    return false;
  }

  @Override
  public void checkInvariants() throws sim.error.InvariantInjuredException {
  }

  @Override
  public void compute(sim.generic.IIncomingPort<?> port) {
  }

  @Override
  public void computeAll(sim.generic.IIncomingPort<?> port) {
  }

  @Override
  protected void sendTick() {
  }
}
