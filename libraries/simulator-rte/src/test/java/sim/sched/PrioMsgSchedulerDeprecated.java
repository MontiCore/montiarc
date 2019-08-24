/* (c) https://github.com/MontiCore/monticore */
/**
 * 
 */
package sim.sched;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sim.IScheduler;
import sim.generic.ISimComponent;
import sim.generic.Message;
import sim.help.PrioMsgComparator;
import sim.port.IInSimPort;

/**
 * TODO: Write me!
 *
 *
 * @deprecated use {@link SchedulerFactory#createPrioScheduler()} instead. PrioMsgSchedulerDeprecated is for tests only.
 */
@Deprecated
public class PrioMsgSchedulerDeprecated extends DefaultSimSchedulerDeprecated implements IScheduler {
    
    protected Map<IInSimPort<?>, List<Message<?>>> bufferedMsgs;
    
    protected Comparator<Message<?>> comparator = new PrioMsgComparator();
    
    /**
     * @deprecated use {@link PrioMsgScheduler} instead. PrioMsgSchedulerDeprecated is for tests only. 
     */
    public PrioMsgSchedulerDeprecated() {
        super();
        bufferedMsgs = new HashMap<IInSimPort<?>, List<Message<?>>>();
        
    }

 
    @Override
    protected boolean processData(ISimComponent comp, IInSimPort<?> port, Message<?> msg) {
        bufferedMsgs.get(port).add(msg);
        return true;
        
    }
    
    @Override
    protected boolean processTick(ISimComponent comp, IInSimPort<?> port) {
        Set<IInSimPort<?>> tickless = comp2tickless.get(comp);
        boolean success = tickless.remove(port);
        
        if (success && tickless.isEmpty()) {
            
            processBufferedMsgs(comp);                
            
            comp.handleTick();
            // wake ports up
            for (IInSimPort<?> p : comp2Ports.get(comp)) {
                if (!p.hasTickReceived()) {
                    tickless.add(p);                                  
                }
                p.wakeUp();
            }
            for (IInSimPort<?> p : comp2Ports.get(comp)) {
                p.processBufferedMsgs();
            }
            
        }
        else {
            success = false;
        }
        
        return success;
        
    }
    
    /**
     * Processes buffered messages for the given component.
     * 
     * @param comp component with buffered messages. 
     */
    protected void processBufferedMsgs(ISimComponent comp) {
        for (IInSimPort<?> port : comp2Ports.get(comp)) {
            List<Message<?>> msgs = new LinkedList<Message<?>>();
            msgs.addAll(bufferedMsgs.get(port));
            Collections.sort(msgs, comparator);
            bufferedMsgs.get(port).clear();
            for (Message<?> msg : msgs) {
                comp.handleMessage(port, msg);
            }
        }
    }

    /* (non-Javadoc)
     * 
     * @see sim.IScheduler#mapPortToComponent(sim.generic.IInSimPort) */
    @Override
    public void setupPort(IInSimPort<?> port) {
        super.setupPort(port);
        if (!this.bufferedMsgs.containsKey(port)) {
            this.bufferedMsgs.put(port, new LinkedList<Message<?>>());
        }
    }
    
    
}
