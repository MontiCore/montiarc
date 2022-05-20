/* (c) https://github.com/MontiCore/monticore */
package sim.port;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sim.dummys.ComponentTimeDummy;
import sim.error.ISimulationErrorHandler;
import sim.error.SimpleErrorHandler;
import sim.message.IStream;
import sim.message.Message;
import sim.message.Tick;
import sim.sched.IScheduler;
import sim.sched.SchedulerFactory;
import sim.serialiser.BackTrackHandler;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for the incoming port implementation.
 */
public class IncomingPortTest {

  protected SimplePort<String> testling;

  protected ComponentTimeDummy comp;

  protected IScheduler scheduler;

  @BeforeEach
  public void setUp() {
    scheduler = SchedulerFactory.createDefaultScheduler();
    comp = new ComponentTimeDummy();
    ISimulationErrorHandler errorHandler = new SimpleErrorHandler();
    comp.setup(scheduler, errorHandler, new BackTrackHandler("incporttest/"));
    testling = new SimplePort<String>();
    testling.setup(comp, scheduler);
  }

  @Test
  public void testGetComponent() {
    assertEquals(comp, comp.getIn().getComponent());
  }

  @Test
  public void testGetIncomingStream() {
    IStream<String> s = testling.stream;
    assertTrue(testling.getIncomingStream().isEmpty());
    assertEquals(s, testling.getIncomingStream());
  }

  @Test
  public void testHasTickReceived() {
    IStream<String> s = testling.stream;
    assertFalse(testling.hasTickReceived());
    s.add(Tick.<String>get());
    assertTrue(testling.hasTickReceived());
    s.pollLastMessage();
    assertFalse(testling.hasTickReceived());
  }

  @Test
  public void testHasUnprocessedMessages() {
    IStream<String> s = testling.stream;
    assertFalse(testling.hasUnprocessedMessages());
    s.add(Tick.<String>get());
    assertTrue(testling.hasUnprocessedMessages());
    s.pollLastMessage();
    assertFalse(testling.hasUnprocessedMessages());
    s.add(Message.of("Hallo"));
    assertTrue(testling.hasUnprocessedMessages());
    s.pollLastMessage();
    assertFalse(testling.hasUnprocessedMessages());
  }

  @Test
  public void testReceiveMessage() {
    String expectedMessage = "Hallo";
    Message<String> msg = Message.of(expectedMessage);
    comp.getIn().accept(msg);
    String actualMessage = comp.getOut().getStream().getLastMessage();
    assertEquals(expectedMessage, actualMessage);
  }

  @Test
  public void testSetup() {
    testling.setup(null, null);
    assertNull(testling.getScheduler());
    assertNull(testling.getComponent());

    IScheduler s = new SimulationScheduler();

    testling.setup(null, s);
    assertEquals(s, testling.getScheduler());
    assertNull(testling.getComponent());

    ComponentTimeDummy c = new ComponentTimeDummy();

    testling.setup(c, s);
    assertEquals(s, testling.getScheduler());
    assertEquals(c, testling.getComponent());
  }
}
