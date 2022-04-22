/* (c) https://github.com/MontiCore/monticore */
package sim.generic;

import org.junit.jupiter.api.Test;
import sim.message.*;

import java.util.Collection;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

public class StreamTest {

  @Test
  public void testIsPrefix() {
    IStream<String> s = new Stream<String>();
    IStream<String> t = new Stream<String>();
    IStream<String> u = new Stream<String>();

    for (int i = 0; i < 100; i++) {
      TickedMessage<String> msg = Message.of("" + i);
      if (i < 50) {
        s.add(msg);
        t.add(msg);
        u.add(msg);
      } else if (i < 80) {
        s.add(msg);
        t.add(msg);
      } else {
        s.add(msg);
      }
    }

    while (s.pollLastMessage() != null) {
    }
    while (t.pollLastMessage() != null) {
    }
    while (u.pollLastMessage() != null) {
    }

    assertTrue(t.isPrefix(s));
    assertTrue(u.isPrefix(s));
    assertTrue(s.isPrefix(s));
    assertTrue(u.isPrefix(t));
    assertFalse(t.isPrefix(u));
    assertFalse(s.isPrefix(t));
    assertFalse(s.isPrefix(u));
    assertFalse(s.isPrefix(null));
  }

  @Test
  public void testFilter() {
    IStream<String> s = new Stream<String>();

    for (int i = 0; i < 10; i++) {
      TickedMessage<String> msg = Message.of("" + i);
      s.add(msg);
    }
    while (s.pollLastMessage() != null) {

    }

    Collection<String> filter = null;
    assertEquals(0, s.filter(filter).size());
    filter = new HashSet<String>();
    assertEquals(0, s.filter(filter).size());
    filter.add("0");
    assertEquals(1, s.filter(filter).size());
    assertTrue(s.filter(filter).contains("0"));
    assertFalse(s.filter(filter).contains("1"));

    s.add(Message.of("0"));
    assertEquals(1, s.filter(filter).getHistory().size());
    assertEquals(1, s.filter(filter).getBuffer().size());
    assertTrue(s.filter(filter).contains("0"));
    s.pollLastMessage();
    assertEquals(2, s.filter(filter).getHistory().size());

    filter.add("9");
    assertEquals(3, s.filter(filter).getHistory().size());
    assertTrue(s.filter(filter).contains("9"));

    filter.add("notInStream");
    assertEquals(3, s.filter(filter).size());

    assertEquals(s, s.filter(s.getUntimedHistory()));
  }

  @Test
  public void testSubStream() {
    IStream<String> s = new Stream<String>();

    int size = 100;
    int subStart = 50;

    for (int i = 0; i < size; i++) {
      TickedMessage<String> msg = Message.of("" + i);
      s.add(msg);
    }
    while (s.pollLastMessage() != null) {

    }

    IStream<String> subStream = s.subStream(subStart, size);
    assertEquals(0, subStream.getBuffer().size());
    assertEquals(size - subStart, subStream.getHistory().size());

    for (int i = 0; i < (size - subStart); i++) {
      assertFalse(subStream.get(i).isTick());
      String str = ((Message<String>) subStream.get(i)).getData();
      assertEquals("" + (i + subStart), str);
    }
  }

  @Test
  public void testSubStreamPartiallyBuffered() {
    IStream<String> s = new Stream<String>();

    int size = 100;
    int subStart = 50;
    int buffer = 75;

    for (int i = 0; i < size; i++) {
      TickedMessage<String> msg = Message.of("" + i);
      s.add(msg);
    }
    for (int i = 0; i < buffer; i++) {
      s.pollLastMessage();
    }

    IStream<String> subStream = s.subStream(subStart, size);
    assertEquals(buffer - subStart, subStream.getBuffer().size());
    assertEquals(size - buffer, subStream.getHistory().size());

    for (int i = 0; i < (size - buffer); i++) {
      assertFalse(subStream.get(i).isTick());
      String str = ((Message<String>) subStream.get(i)).getData();
      assertEquals("" + (i + subStart), str);
    }
  }

  @Test
  public void testSubStreamUnbufferedBuffered() {
    IStream<String> s = new Stream<String>();

    int size = 100;
    int subStart = 50;
    int buffer = 25;

    for (int i = 0; i < size; i++) {
      TickedMessage<String> msg = Message.of("" + i);
      s.add(msg);
    }
    for (int i = 0; i < buffer; i++) {
      s.pollLastMessage();
    }

    IStream<String> subStream = s.subStream(subStart, size);
    assertEquals(size - subStart, subStream.getBuffer().size());
    assertEquals(0, subStream.getHistory().size());
  }

  @Test
  public void testGetTransmitted() {
    IStream<String> s = new Stream<String>();

    int size = 100;
    int buffer = 25;

    for (int i = 0; i < size; i++) {
      TickedMessage<String> msg = Message.of("" + i);
      s.add(msg);
    }
    for (int i = 0; i < buffer; i++) {
      s.pollLastMessage();
    }

    for (int i = 0; i < buffer; i++) {
      assertFalse(s.get(i).isTick());
      String str = ((Message<String>) s.get(i)).getData();
      assertEquals("" + i, str);
    }
  }

  @Test
  public void testGetNotTransmitted() {
    IStream<String> s = new Stream<String>();

    int size = 100;
    int buffer = 25;

    for (int i = 0; i < size; i++) {
      TickedMessage<String> msg = Message.of("" + i);
      s.add(msg);
    }
    for (int i = 0; i < buffer; i++) {
      s.pollLastMessage();
    }

    for (int i = buffer; i < size; i++) {
      try {
        s.get(i);
        fail("expected IndexOutOfBoundsException");
      } catch (Exception e) {
        assertEquals(IndexOutOfBoundsException.class, e.getClass());
      }
    }
  }

  @Test
  public void testCopy() {
    IStream<String> s = new Stream<String>();
    int size = 100;
    int buffer = 25;

    for (int i = 0; i < size; i++) {
      TickedMessage<String> msg = Message.of("" + i);
      s.add(msg);
      if (i < buffer) {
        s.pollLastMessage();
      }
    }

    IStream<String> copy = s.copy();

    assertEquals(s.size(), copy.size());
    assertEquals(s.getBuffer().size(), copy.getBuffer().size());
    assertEquals(s.getHistory().size(), copy.getHistory().size());
    assertEquals(s, copy);
  }

  @Test
  public void testFirstTimeIntervalOf() {
    IStream<Integer> testee = new Stream<Integer>();
    for (int i = 0; i < 100; i++) {
      testee.add(Message.of(i));
      testee.add(Tick.<Integer>get());
      testee.pollLastMessage();
      testee.pollLastMessage();
    }

    for (int i = 0; i < 100; i++) {
      assertEquals(i, testee.firstTimeIntervalOf(i));
    }
  }

  @Test
  public void testFirstTimeIntervalOfNotIncluded() {
    IStream<Integer> testee = new Stream<Integer>();
    for (int i = 0; i < 100; i++) {
      testee.add(Message.of(i));
      testee.add(Tick.<Integer>get());
      testee.pollLastMessage();
      testee.pollLastMessage();
    }
    assertEquals(-1, testee.firstTimeIntervalOf(101));
  }

  @Test
  public void testTimeIntervalsOfOnce() {
    IStream<Integer> testee = new Stream<Integer>();
    for (int i = 0; i < 100; i++) {
      testee.add(Message.of(i));
      testee.add(Tick.<Integer>get());
      testee.pollLastMessage();
      testee.pollLastMessage();
    }
    for (int i = 0; i < 100; i++) {
      int[] inter = testee.timeIntervalsOf(i);
      assertEquals(1, inter.length);
      assertEquals(i, inter[0]);
    }
  }

  @Test
  public void testTimeIntervalsOfManyInDifferentIntervals() {
    IStream<Integer> testee = new Stream<Integer>();
    int msg = 17;
    int expectedAmount = 100;
    for (int i = 0; i < expectedAmount; i++) {
      testee.add(Message.of(msg));
      testee.add(Tick.<Integer>get());
      testee.pollLastMessage();
      testee.pollLastMessage();
    }
    int[] inter = testee.timeIntervalsOf(msg);
    assertEquals(expectedAmount, inter.length);
    for (int i = 0; i < expectedAmount; i++) {
      assertEquals(i, inter[i]);
    }
  }

  @Test
  public void testTimeIntervalsOfManyInSameInterval() {
    IStream<Integer> testee = new Stream<Integer>();
    int msg = 17;
    int expectedAmount = 1;
    for (int i = 0; i < 100; i++) {
      testee.add(Message.of(msg));
      testee.pollLastMessage();
    }
    int[] inter = testee.timeIntervalsOf(msg);
    assertEquals(expectedAmount, inter.length);
    assertEquals(0, inter[0]);
  }

  @Test
  public void testTimeIntervalsOfNotIncluded() {
    IStream<Integer> testee = new Stream<Integer>();
    for (int i = 0; i < 100; i++) {
      testee.add(Message.of(i));
      testee.add(Tick.<Integer>get());
      testee.pollLastMessage();
      testee.pollLastMessage();
    }
    int[] inter = testee.timeIntervalsOf(101);
    assertEquals(0, inter.length);
  }
}
