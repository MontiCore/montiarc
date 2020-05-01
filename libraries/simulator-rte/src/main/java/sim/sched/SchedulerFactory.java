/* (c) https://github.com/MontiCore/monticore */
package sim.sched;

import java.util.BitSet;

import sim.IScheduler;

/**
 * Factory that produces {@link IScheduler}s for MontiArc simulations.
 * 
 */
public class SchedulerFactory {
    
    private static SchedulerFactory theInstance = new SchedulerFactory();
    
    /**
     * @return a default {@link IScheduler} that uses a {@link BitSet} to remember tickless ports.
     */
    public static IScheduler createBitSetScheduler() {
        return theInstance.doCreateBitSetScheduler();
    }
    
    /**
     * @return a default {@link IScheduler} most suitable for common MontiArc simulations.
     */
    public static IScheduler createDefaultScheduler() {
        return createPortMapNoMapScheduler();
    }
    
    /**
     * @return a {@link PrioMsgScheduler}.
     */
    public static IScheduler createPrioScheduler() {
        return theInstance.doCreatePrioScheduler();
    }
    
    /**
     * @return a single in scheduler optimized for components with a single input port.
     */
    public static IScheduler createSingleInScheduler() {
        return theInstance.doCreateSingleInScheduler();
    }
    
    /**
     * Creates and registers a {@link SlaveScheduler}.
     * 
     * @return created {@link SlaveScheduler}.
     */
    public static IScheduler createSlaveScheduler() {
        return MasterScheduler.createScheduler();
    }
    
    /**
     * @return a scheduler with default scheduling strategy that uses a {@link IPortMap} to store
     * tickfree ports.
     */
    public static IScheduler createPortMapScheduler() {
        return theInstance.doCreatePortMapScheduler();
    }
    
    protected IScheduler doCreatePortMapScheduler() {
        return new SimSchedulerPortMap();
    }
    
    /**
     * @return a scheduler with default scheduling strategy that uses a {@link IPortMap} to store
     * tickfree ports and uses a linked list instead of a map to store component related data
     * structures.
     */
    public static IScheduler createPortMapNoMapScheduler() {
        return theInstance.doCreatePortMapNoMapScheduler();
    }
    
    /**
     * @return a scheduler with default scheduling strategy that uses a {@link IPortMap} to store
     * tickfree ports and uses a linked list instead of a map to store component related data
     * structures.
     */
    protected IScheduler doCreatePortMapNoMapScheduler() {
        return new SimSchedulerPortMapNoMaps();
    }
    
    /**
     * Creates and registers a {@link SlaveScheduler} with the given priority.
     * 
     * @param prio priority of the produces scheduler.
     * @return created {@link SlaveScheduler}.
     */
    public static IScheduler createSlaveScheduler(int prio) {
        return MasterScheduler.createScheduler(prio);
    }
    
    /**
     * @return a default {@link IScheduler} that uses a {@link BitSet} to remember tickless ports.
     */
    protected IScheduler doCreateBitSetScheduler() {
        return new SimSchedulerBitSet();
    }
    
    /**
     * @return a {@link PrioMsgScheduler}.
     */
    protected IScheduler doCreatePrioScheduler() {
        return new PrioMsgScheduler();
    }
    
    /**
     * @return a single in scheduler optimized for components with a single input port.
     */
    protected IScheduler doCreateSingleInScheduler() {
        return new SingleInScheduler();
    }
    
}
