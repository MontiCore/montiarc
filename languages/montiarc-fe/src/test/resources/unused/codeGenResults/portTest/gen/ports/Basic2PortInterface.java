package portTest.gen.ports;

public interface Basic2PortInterface {

  public sim.generic.IIncomingPort<java.lang.Integer> getInteger();

  public sim.generic.IIncomingPort<java.lang.String> getString();

  public sim.generic.IIncomingPort<java.lang.Boolean> getBool();

  public void integerMessageReceived(sim.generic.Message<java.lang.Integer> message);

  public void stringMessageReceived(sim.generic.Message<java.lang.String> message);

  public void boolMessageReceived(sim.generic.Message<java.lang.Boolean> message);
}