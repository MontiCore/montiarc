package portTest.gen.ports;

public interface ArchPortInterface {

  public java.util.Set<sim.generic.IIncomingPort<java.lang.String>> getStr();

  public java.util.Set<sim.generic.IIncomingPort<java.lang.Integer>> getInteger();

  public sim.generic.IOutgoingPort<java.lang.Boolean> getBool();

}