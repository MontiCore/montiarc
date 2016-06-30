package portTest.gen.ports;

public interface BasicPortInterface extends
    sim.generic.SimpleInPortInterface<java.lang.String> {

  public sim.generic.IIncomingPort<java.lang.String> getString();

  public sim.generic.IOutgoingPort<java.lang.Boolean> getBool();
}