/**
 * 
 */
package sim.help;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import sim.generic.Message;
import sim.generic.PrioMessage;

/**
 * Tests for {@link PrioMsgComparator}.
 *
 * <br>
 * <br>
 * Copyright (c) 2011 RWTH Aachen. All rights reserved.
 *
 * @author  (last commit) $Author: ahaber $
 * @version $Date: 2015-02-05 17:20:14 +0100 (Do, 05 Feb 2015) $<br>
 *          $Revision: 3114 $
 */
public class PrioMsgComparatorTest {
    
    protected PrioMsgComparator testling = new PrioMsgComparator();
    
    /**
     * Test method for {@link sim.help.PrioMsgComparator#compare(sim.generic.Message, sim.generic.Message).
     */
    @Test
    public void testCompare_Prio_Message_Message() {
        assertEquals(0, testling.compare(Message.of("Hallo"), Message.of("Test")));
        assertEquals(0, testling.compare(Message.<String> of("Test"), Message.<String> of("Hallo")));
    }
    
    /**
     * Test method for {@link sim.help.PrioMsgComparator#compare(sim.generic.Message, sim.generic.Message).
     */
    @Test
    public void testCompare_Prio_Message_PrioMessageDefault() {
        assertEquals(0, testling.compare(Message.of("Hallo"), new PrioMessage<String>("Hallo")));
        assertEquals(0, testling.compare(new PrioMessage<String>("Hallo"), Message.of("Hallo")));
    }
    
    /**
     * Test method for {@link sim.help.PrioMsgComparator#compare(sim.generic.Message, sim.generic.Message).
     */
    @Test
    public void testCompare_Prio_Message_PrioMessage() {
        assertTrue(testling.compare(Message.of("Hallo"), new PrioMessage<String>("Hallo", PrioMessage.MIN_PRIORITY)) < 0);
        assertTrue(testling.compare(new PrioMessage<String>("Hallo", PrioMessage.MAX_PRIORITY), Message.of("Hallo")) < 0);
        
        assertTrue(testling.compare(new PrioMessage<String>("Hallo", PrioMessage.MIN_PRIORITY), Message.of("Hallo")) > 0);
        assertTrue(testling.compare(Message.of("Hallo"), new PrioMessage<String>("Hallo", PrioMessage.MAX_PRIORITY)) > 0);
        
    }
    
    /**
     * Test method for {@link sim.help.PrioMsgComparator#compare(sim.generic.Message, sim.generic.Message).
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
