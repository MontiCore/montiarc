/* (c) https://github.com/MontiCore/monticore */
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
 * Tests for a {@link TimedObservablePort}. 
 * 
 */
public class TimedObservablePortTest {
    
    protected TimedObservablePort<String> testling;
    protected TestObserver observer;
    
    @Before
    public void setUp() {
        testling = new TimedObservablePort<String>();
        observer = new TestObserver();
        testling.addObserver(observer);
    }
    
    @Test
    public void testAcceptTicks() {
        Tick<String> tick = Tick.<String> get();
        testling.accept(tick);
        
        assertEquals(1, observer.callAmount);
        assertEquals(tick, observer.arguments.get(0));
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
