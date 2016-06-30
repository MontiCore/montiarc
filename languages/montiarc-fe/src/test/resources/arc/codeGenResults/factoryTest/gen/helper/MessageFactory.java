package factoryTest.gen.helper;

public class MessageFactory implements sim.generic.IMessageFactory {

  private static MessageFactory theInstance;
  protected java.util.Map<java.lang.String, sim.generic.Tick<?>> tickMap;

  private MessageFactory() {
    tickMap = new java.util.HashMap<java.lang.String, sim.generic.Tick<?>>();
    setUpMap();
  }

  public static MessageFactory getInstance() {
    if (theInstance == null) {
      theInstance = new MessageFactory();
    }
    return theInstance;
  }

  @SuppressWarnings("unchecked")
  public <T> sim.generic.Tick<T> getTick(String tickType) {
    return (sim.generic.Tick<T>) tickMap.get(tickType);
  }

  protected void setUpMap() {
    tickMap.put("java.lang.Boolean", new sim.generic.Tick<java.lang.Boolean>());
    tickMap.put("java.lang.String", new sim.generic.Tick<java.lang.String>());
    tickMap.put("java.lang.Integer", new sim.generic.Tick<java.lang.Integer>());
  }

  @Override
  public <T> sim.generic.TickedMessage<T> getMessage(T data) {
    return new sim.generic.Message<T>(data);
  }
}