package componentTest.gen;

public class ArchOuter extends sim.generic.AComponent implements
    componentTest.gen.interfaces.IArchOuter {

  protected componentTest.gen.interfaces.ISimpleOutGeneric<java.lang.Integer> simpleOutGeneric;

  protected componentTest.gen.interfaces.IArchInner archInner;

  protected componentTest.gen.interfaces.ISimpleOut simpleOut;

  protected componentTest.gen.interfaces.ISimple simple;

  public ArchOuter() {
    super();
  }

  @Override
  public java.util.Set<sim.generic.IIncomingPort<java.lang.String>> getStr2() {
    java.util.Set<sim.generic.IIncomingPort<java.lang.String>> returnSet = new java.util.HashSet<sim.generic.IIncomingPort<java.lang.String>>();
    returnSet.add(simpleOut.getStr2());
    returnSet.addAll(archInner.getString());
    return returnSet;
  }

  @Override
  public java.util.Set<sim.generic.IIncomingPort<java.lang.String>> getStr1() {
    java.util.Set<sim.generic.IIncomingPort<java.lang.String>> returnSet = new java.util.HashSet<sim.generic.IIncomingPort<java.lang.String>>();
    returnSet.add(simpleOutGeneric.getString());
    returnSet.add(simpleOut.getStr1());
    returnSet.add(simple.getString());
    return returnSet;
  }

  @Override
  public sim.generic.IOutgoingPort<java.lang.Integer> getInt1() {
    return archInner.getIntOut1();
  }

  @Override
  public sim.generic.IOutgoingPort<java.lang.Integer> getInt2() {
    return archInner.getIntOut2();
  }

  @Override
  public sim.generic.IOutgoingPort<java.lang.Integer> getInt3() {
    return simpleOutGeneric.getOutput();
  }

  @Override
  public int getLocalTime() {
    return getInt1().getComponent().getLocalTime();
  }

  @Override
  public void setup(sim.IScheduler scheduler,
      sim.error.ISimulationErrorHandler errorHandler) {
    java.util.Map<java.lang.String, java.lang.String> portInfos = new java.util.HashMap<java.lang.String, java.lang.String>();
    portInfos.put("str2", "java.lang.String");
    portInfos.put("str1", "java.lang.String");
    super.setIncomingPortInformation(portInfos);
    this.simpleOutGeneric = componentTest.gen.factories.SimpleOutGenericFactory
        .getInstance().create(java.lang.Integer.class);
    this.simpleOutGeneric.setName("SimpleOutGeneric simpleOutGeneric");
    this.simpleOutGeneric.setup(scheduler, errorHandler);
    this.archInner = componentTest.gen.factories.ArchInnerFactory.getInstance()
        .create();
    this.archInner.setName("ArchInner archInner");
    this.archInner.setup(scheduler, errorHandler);
    this.simpleOut = componentTest.gen.factories.SimpleOutFactory.getInstance()
        .create();
    this.simpleOut.setName("SimpleOut simpleOut");
    this.simpleOut.setup(scheduler, errorHandler);
    this.simple = componentTest.gen.factories.SimpleFactory.getInstance()
        .create();
    this.simple.setName("Simple simple");
    this.simple.setup(scheduler, errorHandler);

    // connect simpleOut.integer -> simpleOutGeneric.input2;
    this.simpleOut.getInteger().addReceiver(simpleOutGeneric.getInput2());

    // connect simple.integer -> simpleOutGeneric.input1;
    this.simple.getInteger().addReceiver(simpleOutGeneric.getInput1());
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