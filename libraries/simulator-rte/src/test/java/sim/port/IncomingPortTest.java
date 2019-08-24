/* (c) https://github.com/MontiCore/monticore */
/**
 * 
 */
package sim.port;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
    
    
    @Before
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
        Assert.assertEquals(comp, testling.getComponent());
    }
    
    @Test
    public void testGetIncomingStream() {
        IStream<String> s = testling.stream;
        Assert.assertTrue(testling.getIncomingStream().isEmpty());
        Assert.assertEquals(s, testling.getIncomingStream());
        
    }
    
    @Test
    public void testHasTickReceived() {
        IStream<String> s = testling.stream;
        Assert.assertFalse(testling.hasTickReceived());
        s.add(Tick.<String> get());
        Assert.assertTrue(testling.hasTickReceived());
        s.pollLastMessage();
        Assert.assertFalse(testling.hasTickReceived());
    }
    
    @Test
    public void testHasUnprocessedMessages() {
        IStream<String> s = testling.stream;
        Assert.assertFalse(testling.hasUnprocessedMessages());
        s.add(Tick.<String> get());
        Assert.assertTrue(testling.hasUnprocessedMessages());
        s.pollLastMessage();
        Assert.assertFalse(testling.hasUnprocessedMessages());
        s.add(Message.of("Hallo"));
        Assert.assertTrue(testling.hasUnprocessedMessages());
        s.pollLastMessage();
        Assert.assertFalse(testling.hasUnprocessedMessages());
    }
    
    @Test
    public void testReceiveMessage() {
        IStream<String> s = testling.stream;
        Assert.assertTrue(s.isEmpty());
        
        String expectedMessage = "Hallo";
        
        testling.accept(Message.of(expectedMessage));
        String actualMessage = ((Message<String>) s.getHistory().get(0)).getData();
        Assert.assertEquals(expectedMessage, actualMessage);
    }
    
    @Test
    public void testSetup() {
        testling.setup(null, null);
        Assert.assertNull(testling.getScheduler());
        Assert.assertNull(testling.getComponent());
        
        IScheduler s = new SimulationScheduler();
        
        testling.setup(null, s);
        Assert.assertEquals(s, testling.getScheduler());
        Assert.assertNull(testling.getComponent());
        
        ComponentTimeDummy c = new ComponentTimeDummy();
        
        testling.setup(c, s);
        Assert.assertEquals(s, testling.getScheduler());
        Assert.assertEquals(c, testling.getComponent());
    }
    
}
