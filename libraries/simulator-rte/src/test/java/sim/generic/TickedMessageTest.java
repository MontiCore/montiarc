/* (c) https://github.com/MontiCore/monticore */
package sim.generic;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Module test for Ticked Messages.
 * 
 * @author Arne Haber
 * @version 09.12.2008
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
