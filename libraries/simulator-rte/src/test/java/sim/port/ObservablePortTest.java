/* (c) https://github.com/MontiCore/monticore */
package sim.port;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sim.generic.Message;
import sim.generic.Tick;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ObservablePortTest {

  protected ObservablePort<String> testling;
  protected TestObserver observer;

  @BeforeEach
  public void setUp() {
    testling = new ObservablePort<String>();
    observer = new TestObserver();
    testling.addObserver(observer);
  }

  @Test
  public void testAcceptTicks() {
    Tick<String> tick = Tick.<String>get();
    testling.accept(tick);

    assertEquals(0, observer.callAmount);
  }

  @Test
  public void testAcceptData() {
    String data = "Hal-9001";
    testling.accept(data);

    assertEquals(1, observer.callAmount);
    assertEquals(data, observer.arguments.get(0));
  }

  @Test
  public void testAcceptCapsuledData() {
    String data = "Hal-9001";
    testling.accept(Message.of(data));

    assertEquals(1, observer.callAmount);
    assertEquals(data, observer.arguments.get(0));
  }
}
