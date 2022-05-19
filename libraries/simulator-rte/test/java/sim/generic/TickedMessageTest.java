/* (c) https://github.com/MontiCore/monticore */
package sim.generic;

import org.junit.jupiter.api.Test;
import sim.message.Message;
import sim.message.Tick;
import sim.message.TickedMessage;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Module test for Ticked Messages.
 */
public class TickedMessageTest {

  @Test
  public void testIsTick() {
    TickedMessage<String> tick = Tick.get();
    TickedMessage<String> message = Message.of("TEST");
    assertTrue(tick.isTick());
    assertFalse(message.isTick());
  }

}
