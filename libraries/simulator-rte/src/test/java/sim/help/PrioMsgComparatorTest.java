/* (c) https://github.com/MontiCore/monticore */
package sim.help;

import org.junit.jupiter.api.Test;
import sim.message.Message;
import sim.message.PrioMessage;
import sim.schedhelp.PrioMsgComparator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link PrioMsgComparator}.
 */
public class PrioMsgComparatorTest {

  protected PrioMsgComparator testling = new PrioMsgComparator();

  /**
   * Test method for {@link PrioMsgComparator#compare(Message, Message).
   */
  @Test
  public void testCompare_Prio_Message_Message() {
    assertEquals(0, testling.compare(Message.of("Hallo"), Message.of("Test")));
    assertEquals(0, testling.compare(Message.<String>of("Test"), Message.<String>of("Hallo")));
  }

  /**
   * Test method for {@link PrioMsgComparator#compare(Message, Message).
   */
  @Test
  public void testCompare_Prio_Message_PrioMessageDefault() {
    assertEquals(0, testling.compare(Message.of("Hallo"), new PrioMessage<String>("Hallo")));
    assertEquals(0, testling.compare(new PrioMessage<String>("Hallo"), Message.of("Hallo")));
  }

  /**
   * Test method for {@link PrioMsgComparator#compare(Message, Message).
   */
  @Test
  public void testCompare_Prio_Message_PrioMessage() {
    assertTrue(testling.compare(Message.of("Hallo"), new PrioMessage<String>("Hallo", PrioMessage.MIN_PRIORITY)) < 0);
    assertTrue(testling.compare(new PrioMessage<String>("Hallo", PrioMessage.MAX_PRIORITY), Message.of("Hallo")) < 0);

    assertTrue(testling.compare(new PrioMessage<String>("Hallo", PrioMessage.MIN_PRIORITY), Message.of("Hallo")) > 0);
    assertTrue(testling.compare(Message.of("Hallo"), new PrioMessage<String>("Hallo", PrioMessage.MAX_PRIORITY)) > 0);

  }

  /**
   * Test method for {@link PrioMsgComparator#compare(Message, Message).
   */
  @Test
  public void testCompare_Prio_PrioMessage_PrioMessage() {
    assertTrue(testling.compare(new PrioMessage<String>("Hallo", PrioMessage.MAX_PRIORITY),
      new PrioMessage<String>("Hallo", PrioMessage.MIN_PRIORITY)) < 0);
    assertTrue(testling.compare(new PrioMessage<String>("Hallo", PrioMessage.MIN_PRIORITY),
      new PrioMessage<String>("Hallo", PrioMessage.MAX_PRIORITY)) > 0);

    assertTrue(testling.compare(new PrioMessage<String>("Hallo", PrioMessage.MAX_PRIORITY),
      new PrioMessage<String>("Hallo", PrioMessage.MAX_PRIORITY)) == 0);
    assertTrue(testling.compare(new PrioMessage<String>("Hallo", PrioMessage.MIN_PRIORITY),
      new PrioMessage<String>("Hallo", PrioMessage.MIN_PRIORITY)) == 0);
  }
}
