/* (c) https://github.com/MontiCore/monticore */
package sim.port;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sim.dummys.ComponentPortTest;
import sim.error.SimpleErrorHandler;
import sim.message.IStream;
import sim.message.Message;
import sim.message.Tick;
import sim.message.TickedMessage;
import sim.sched.IScheduler;
import sim.serialiser.BackTrackHandler;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for incoming stream methods {@link IStream}.
 */
public class IncomingStreamTest {
  protected ComponentPortTest comp;

  protected SimplePort<String> testling;

  @BeforeEach
  public void setUp() {
    comp = new ComponentPortTest();
    testling = new SimplePort<String>();
    IScheduler s = new SimulationScheduler();
    comp.setup(s, new SimpleErrorHandler(), new BackTrackHandler("incstreamtest/"));
    testling.setup(comp, s);
  }

  @Test
  public void testGetHistory() {
    assertEquals(0, testling.getIncomingStream().getHistory().size());
    testling.accept(Tick.<String>get());
    assertEquals(1, testling.getIncomingStream().getHistory().size());
    testling.accept(Message.of("TEST"));
    assertEquals(2, testling.getIncomingStream().getHistory().size());
    testling.accept(Tick.<String>get());
    assertEquals(3, testling.getIncomingStream().getHistory().size());
    testling.accept(Tick.<String>get());
    assertEquals(4, testling.getIncomingStream().getHistory().size());
    assertNull(testling.getIncomingStream().pollLastMessage());
    assertEquals(4, testling.getIncomingStream().getHistory().size());
    assertNull(testling.getIncomingStream().pollLastMessage());

    List<TickedMessage<String>> history = testling.getIncomingStream().getHistory();
    assertTrue(history.get(0).isTick());
    assertFalse(history.get(1).isTick());
    assertEquals("TEST", ((Message<String>) history.get(1)).getData());
    assertTrue(history.get(2).isTick());
    assertTrue(history.get(3).isTick());

  }

  @Test
  public void testGetHistoryN() {
    for (int i = 0; i < 10; i++) {
      assertEquals(0, testling.getIncomingStream().getHistory(i).size());
    }
    testling.accept(Tick.<String>get());
    testling.accept(Message.of("TEST"));
    testling.accept(Tick.<String>get());
    testling.accept(Tick.<String>get());

    for (int i = 0; i < 10; i++) {
      assertEquals((i < 4 ? i : 4), testling.getIncomingStream().getHistory(i).size());
    }

    assertEquals(0, testling.getIncomingStream().getHistory(-1).size());

    assertTrue(testling.getIncomingStream().getHistory(4).get(0).isTick());
    assertFalse(testling.getIncomingStream().getHistory(4).get(1).isTick()); // TEST
    // msg
    assertTrue(testling.getIncomingStream().getHistory(4).get(2).isTick());
    assertTrue(testling.getIncomingStream().getHistory(4).get(3).isTick());

    assertFalse(testling.getIncomingStream().getHistory(3).get(0).isTick()); // TEST
    // msg
    assertTrue(testling.getIncomingStream().getHistory(3).get(1).isTick());
    assertTrue(testling.getIncomingStream().getHistory(3).get(2).isTick());

    assertTrue(testling.getIncomingStream().getHistory(2).get(0).isTick());
    assertTrue(testling.getIncomingStream().getHistory(2).get(1).isTick());

    assertTrue(testling.getIncomingStream().getHistory(1).get(0).isTick());
  }

  @Test
  public void testGetLastMessage() {
    // fill input stream
    String lastMessage = "";
    for (int i = 0; i < 101; i++) {
      if (i % 4 == 0) {
        testling.accept(Tick.<String>get());
      } else {
        lastMessage = "Message : " + ((i / 4) + 1) + "." + (i % 4);
        testling.accept(Message.of(lastMessage));
      }
      if (i != 0) {
        assertEquals(lastMessage, testling.getIncomingStream().getLastMessage());
      } else {
        assertNull(testling.getIncomingStream().getLastMessage());
      }
    }
  }

  @Test
  public void testGetLastNTimeSlices() {
    // fill input stream
    for (int i = 0; i < 101; i++) {
      if (i % 4 == 0) {
        testling.accept(Tick.<String>get());
        comp.setTime(comp.getLocalTime() + 1);
      } else {
        testling.accept(Message.of("Message : " + ((i / 4) + 1) + "." + (i % 4)));
      }
    }

    assertEquals(testling.getIncomingStream().getUntimedHistory(),
        testling.getIncomingStream().getLastNTimeIntervals(25));
    assertEquals(testling.getIncomingStream().getUntimedHistory(),
        testling.getIncomingStream().getLastNTimeIntervals(26));
    assertEquals(testling.getIncomingStream().getUntimedHistory(),
        testling.getIncomingStream().getLastNTimeIntervals(100));
    assertNotSame(testling.getIncomingStream().getUntimedHistory(),
        testling.getIncomingStream().getLastNTimeIntervals(24));
    for (int i = 0; i < 26; i++) {
      List<String> slices = testling.getIncomingStream().getLastNTimeIntervals(i);
      assertEquals(i * 3, slices.size());
      int index = 25 - i;
      for (int j = 0; j < slices.size(); j++) {
        if (j % 3 == 0) {
          index += 1;
        }
        assertEquals("Message : " + index + "." + (j % 3 + 1), slices.get(j));
      }
    }
  }

  @Test
  public void testGetLastTimeSlice() {
    for (int i = 0; i < 100; i++) {
      if (i % 4 == 0) {
        testling.accept(Tick.<String>get());
        comp.setTime(comp.getLocalTime() + 1);
      } else {
        testling.accept(Message.of("" + i));
      }
      testling.getIncomingStream().pollLastMessage();
      if (i % 4 == 1) {
        assertTrue(testling.getIncomingStream().getLastTimeInterval().contains("" + i));
      } else if (i % 4 == 2) {
        assertTrue(testling.getIncomingStream().getLastTimeInterval().contains("" + i));
        assertTrue(testling.getIncomingStream().getLastTimeInterval().contains("" + (i - 1)));
      } else if (i % 4 == 3) {
        assertTrue(testling.getIncomingStream().getLastTimeInterval().contains("" + i));
        assertTrue(testling.getIncomingStream().getLastTimeInterval().contains("" + (i - 1)));
        assertTrue(testling.getIncomingStream().getLastTimeInterval().contains("" + (i - 2)));
      }
    }
  }

  @Test
  public void testGetTimeSlice() {
    for (int i = 0; i < 101; i++) {
      if (i % 4 == 0) {
        testling.accept(Tick.<String>get());
      } else {
        testling.accept(Message.of("Message : " + ((i / 4) + 1) + "." + (i % 4)));
      }
    }

    assertEquals(0, testling.getIncomingStream().getTimeInterval(0).size());
    for (int i = 1; i < 26; i++) {
      List<String> slice = testling.getIncomingStream().getTimeInterval(i);
      assertEquals(3, slice.size());
      for (int j = 0; j < 3; j++) {
        assertEquals("Message : " + i + "." + (j + 1), slice.get(j));
      }
    }
  }

  @Test
  public void testGetTimeSliceFromTo() {
    // fill input stream
    for (int i = 0; i < 101; i++) {
      if (i % 4 == 0) {
        testling.accept(Tick.<String>get());
      } else {
        testling.accept(Message.of("Message : " + ((i / 4) + 1) + "." + (i % 4)));
      }
    }

    assertEquals(testling.getIncomingStream().getUntimedHistory(), testling.getIncomingStream().getTimeInterval(0, 25));
    assertEquals(testling.getIncomingStream().getTimeInterval(5), testling.getIncomingStream().getTimeInterval(5, 4));
    for (int i = 1; i < 26; i++) {
      for (int j = i; j < 26; j++) {
        List<String> slices = testling.getIncomingStream().getTimeInterval(i, j);
        assertEquals((Math.abs(j - i) + 1) * 3, slices.size());
        assertEquals("Message : " + i + ".1", slices.get(0));
        assertEquals("Message : " + j + ".3", slices.get(slices.size() - 1));
      }
    }
  }

  @Test
  public void testGetTimeUnit() {
    List<String> sentMessages = new LinkedList<String>();

    // fill input stream
    for (int i = 0; i < 101; i++) {
      if (i % 2 == 0) {
        testling.accept(Tick.<String>get());
      } else {
        String data = "Message : " + ((i / 2) + 1) + "." + (i % 2);
        testling.accept(data);
        sentMessages.add(data);
      }

    }

    // check time units
    for (int i = 0; i < sentMessages.size(); i++) {
      String sentMessage = sentMessages.get(i);
      assertEquals(i + 1, testling.getIncomingStream().firstTimeIntervalOf(sentMessage));
    }
  }

  @Test
  public void testGetUntimedHistory() {
    // fill input stream
    for (int i = 0; i < 101; i++) {
      if (i % 2 == 0) {
        testling.accept(Tick.<String>get());
      } else {
        testling.accept(Message.of("Message : " + ((i / 2) + 1)));
      }
    }

    List<String> untimedHistory = testling.getIncomingStream().getUntimedHistory();
    assertEquals(50, untimedHistory.size());
    for (int i = 0; i < untimedHistory.size(); i++) {
      assertEquals("Message : " + (i + 1), untimedHistory.get(i));
    }
  }

  @Test
  public void testStreamContains() {
    testling.accept(Message.of("TEST"));
    assertTrue(testling.getIncomingStream().contains("TEST"));
    assertFalse(testling.getIncomingStream().contains("BLA"));
  }

  @Test
  public void testStreamContainsAll() {
    testling.accept(Message.of("TEST"));
    for (int i = 0; i < 10; i++) {
      testling.accept(Message.of("" + i));
    }

    List<String> collection = new LinkedList<String>();

    for (int i = 9; i >= 0; i--) {
      collection.add("" + i);
      assertTrue(testling.getIncomingStream().containsAll(collection));
    }

    collection.add("TEST");
    assertTrue(testling.getIncomingStream().containsAll(collection));

    collection.add("NOT INCLUDED");
    assertFalse(testling.getIncomingStream().containsAll(collection));

    assertTrue(testling.getIncomingStream().containsAll(new LinkedList<String>()));
    assertFalse(testling.getIncomingStream().containsAll(null));
  }

  @Test
  public void testStreamCount() {
    testling.accept(Message.of("TEST"));
    assertEquals(0, testling.getIncomingStream().count("BLA"));
    assertEquals(1, testling.getIncomingStream().count("TEST"));

    testling.accept(Message.of("TEST"));
    assertEquals(2, testling.getIncomingStream().count("TEST"));

    for (int i = 0; i < 100; i++) {
      assertEquals(i, testling.getIncomingStream().count("SOMETHING"));
      testling.accept(Message.of("SOMETHING"));
    }
  }
}
