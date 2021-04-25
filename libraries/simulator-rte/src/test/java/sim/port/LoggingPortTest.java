/* (c) https://github.com/MontiCore/monticore */
/**
 * 
 */
package sim.port;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import sim.generic.ATimedComponent;
import sim.generic.Message;
import sim.generic.Tick;
import sim.sched.SimSchedulerHashSet;

/**
 * Tests for the logging port. 
 * 
 * 
 */
public class LoggingPortTest {
    
    /**
     * Amount of messages.
     */
    protected static final int AMOUNT = 100;
    
    
    @Test
    public void testLogDataAccept() {
        Logger logger = mock(Logger.class);
        LoggingPort<String> testee = new LoggingPort<String>(logger);
        
        String message = "Hallo";
        
        for (int i = 0; i < AMOUNT; i++) {
            testee.accept(message);
        }
        verify(logger, times(AMOUNT)).debug("accept " + message);
    }
    
    @Test
    public void testLogTicksAccept() {
        Logger logger = mock(Logger.class);
        LoggingPort<String> testee = new LoggingPort<String>(logger);
        
        Tick<String> tick = Tick.<String> get();
        
        for (int i = 0; i < AMOUNT; i++) {
            testee.accept(tick);
        }
        verify(logger, times(AMOUNT)).debug("accept Tk");
    }
    
    @Test
    public void testLogDataSend() {
        Logger logger = mock(Logger.class);
        LoggingPort<String> testee = new LoggingPort<String>(logger);
        
        Message<String> message = Message.of("Hallo");
        
        for (int i = 0; i < AMOUNT; i++) {
            testee.send(message);
        }
        verify(logger, times(AMOUNT)).debug("send " + message);
    }
    
    @Test
    public void testLogTicksSend() {
        Logger logger = mock(Logger.class);
        LoggingPort<String> testee = new LoggingPort<String>(logger);
        
        Tick<String> tick = Tick.<String> get();
        
        for (int i = 0; i < AMOUNT; i++) {
            testee.send(tick);
        }
        verify(logger, times(AMOUNT)).debug("send Tk");
    }
    
    @Test
    public void testComponentInfo() {
        Logger logger = mock(Logger.class);
        LoggingPort<String> testee = new LoggingPort<String>(logger);
        ATimedComponent comp = mock(ATimedComponent.class);
        when(comp.toString()).thenReturn("TestComponent");
        
        testee.setup(comp, new SimSchedulerHashSet());
        
        Message<String> message = Message.of("Hallo");
        
        for (int i = 0; i < AMOUNT; i++) {
            when(comp.getLocalTime()).thenReturn(i);
            testee.send(message);
        }
        for (int i = 0; i < AMOUNT; i++) {
            verify(logger, times(1)).debug("accept TestComponent(" + i + ") -> " + message);
        }
    }
    
}
