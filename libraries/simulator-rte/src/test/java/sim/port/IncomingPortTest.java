/* (c) https://github.com/MontiCore/monticore */
/**
 * 
 */
package sim.port;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sim.IScheduler;
import sim.dummys.ComponentTimeDummy;
import sim.generic.IStream;
import sim.generic.Message;
import sim.generic.Tick;

/**
 * Test for the incoming port implementation. 
 * 
 */
public class IncomingPortTest {
    
    protected SimplePort<String> testling;
    
    protected ComponentTimeDummy comp;
    
    protected IScheduler scheduler;
    
    public IInPort<?> computedPort;
    
    
    @BeforeEach
    public void setUp() {
        scheduler = new SimulationScheduler();
        comp = new ComponentTimeDummy();
//        comp = new ComponentTimeDummy(){
//            @Override
//            public void compute(IIncomingPort<?> port) {
//                super.compute(port);
//                computedPort = port;
//            }
//        };
        
        testling = new SimplePort<String>();
        testling.setup(comp, scheduler);
    }
    
    @Test
    public void testGetComponent() {
        assertEquals(comp, testling.getComponent());
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
        s.add(Tick.<String> get());
        assertTrue(testling.hasTickReceived());
        s.pollLastMessage();
        assertFalse(testling.hasTickReceived());
    }
    
    @Test
    public void testHasUnprocessedMessages() {
        IStream<String> s = testling.stream;
        assertFalse(testling.hasUnprocessedMessages());
        s.add(Tick.<String> get());
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
        IStream<String> s = testling.stream;
        assertTrue(s.isEmpty());
        
        String expectedMessage = "Hallo";
        
        testling.accept(Message.of(expectedMessage));
        String actualMessage = ((Message<String>) s.getHistory().get(0)).getData();
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
