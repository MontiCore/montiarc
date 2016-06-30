/**
 * 
 */
package sim.port;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import sim.generic.Message;
import sim.generic.Tick;

/**
 * TODO: Write me!
 *
 * <br>
 * <br>
 * Copyright (c) 2012 RWTH Aachen. All rights reserved.
 *
 * @author  (last commit) $Author: ahaber $
 * @version $Date: 2015-02-05 17:20:14 +0100 (Do, 05 Feb 2015) $<br>
 *          $Revision: 3114 $
 */
public class ObservablePortTest {
    
    protected ObservablePort<String> testling;
    protected TestObserver observer;
    
    @Before
    public void setUp() {
        testling = new ObservablePort<String>();
        observer = new TestObserver();
        testling.addObserver(observer);
    }
    
    @Test
    public void testAcceptTicks() {
        Tick<String> tick = Tick.<String> get();
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
