/**
 * 
 */
package sim.sched;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import sim.IScheduler;
import sim.help.DescendingIntComparator;
import sim.port.IForwardPort;
import sim.port.IInPort;
import sim.port.IInSimPort;
import sim.port.IOutSimPort;
import sim.port.IPortFactory;
import sim.port.MasterSlavePortFactory;

/**
 * A Master Scheduler that controls several slave scheduler. Each slave can be
 * associated with a priority. High priorities are preferred, if buffered
 * messages of silent ports are to be processed (see activate method). <br>
 * <br>
 * Copyright (c) 2011 RWTH Aachen. All rights reserved.
 * 
 * @author (last commit) $Author: ahaber $
 * @version $Date: 2014-06-24 14:38:16 +0200 (Di, 24 Jun 2014) $<br>
 *          $Revision: 2874 $
 */
final class MasterScheduler implements IPortFactory {
    
    private final Set<IInPort<?>> active;
    
    private final Map<IScheduler, Queue<IInSimPort<?>>> sched2silentPorts;
    
    private final SortedMap<Integer, Set<IScheduler>> prio2scheds;
    
    private static MasterScheduler theInstance = new MasterScheduler();
    
    public static final int DEFAULT_PRIORITY = 500;
    
    public static final int MIN_PRIORITY = 0;
    
    public static final int MAX_PRIORITY = 1000;
    
    /**
     * Used to produce ports.
     */
    protected IPortFactory portFactory;
    
    /**
     * 
     */
    private MasterScheduler() {
        active = new HashSet<IInPort<?>>();
        sched2silentPorts = new HashMap<IScheduler, Queue<IInSimPort<?>>>();
        
        Comparator<Integer> reverseComparator = new DescendingIntComparator();
        prio2scheds = new TreeMap<Integer, Set<IScheduler>>(reverseComparator);
        portFactory = new MasterSlavePortFactory();
        
    }
    
    /**
     * 
     * @return a scheduler with default priority
     */
    protected static IScheduler createScheduler() {
        return theInstance.doCreateScheduler(DEFAULT_PRIORITY);
    }
    
    /**
     * Creates a scheduler with the given priority.
     * 
     * @param prio priority
     * @return scheduler with the given priority
     */
    protected static IScheduler createScheduler(int prio) {
        return theInstance.doCreateScheduler(prio);
    }
    
    /**
     * Creates a scheduler with the given priority.
     * 
     * @param priority priority
     * @return scheduler with the given priority
     */
    protected IScheduler doCreateScheduler(int priority) {
        int prio = priority;
        if (prio < MIN_PRIORITY) {
            prio = MIN_PRIORITY;
        }
        else if (prio > MAX_PRIORITY) {
            prio = MAX_PRIORITY;
        }
        
        if (!prio2scheds.containsKey(prio)) {
            prio2scheds.put(prio, new HashSet<IScheduler>());
        }
        IScheduler result = new SlaveScheduler(this);
        sched2silentPorts.put(result, new LinkedList<IInSimPort<?>>());
        
        prio2scheds.get(prio).add(result);
        
        return result;
    }
    
    protected void setActive(IInPort<?> port) {
        active.add(port);
    }
    
    protected void setInactive(IInPort<?> port) {
        active.remove(port);
    }
    
    protected boolean isActive(IInPort<?> port) {
        return active.contains(port);
    }
    
    
    protected void register(IScheduler sched, IInSimPort<?> silentPort) {
        Queue<IInSimPort<?>> silentPorts = sched2silentPorts.get(sched);
        if (!silentPorts.contains(silentPort)) {
            silentPorts.offer(silentPort);
        }
    }
    
    protected void activate() {
        for (Integer currentPrio : prio2scheds.keySet()) {
            for (IScheduler sched : prio2scheds.get(currentPrio)) {
                for (IInSimPort<?> p : sched2silentPorts.get(sched)) {
                    p.processBufferedMsgs();
                }
            }
        }
    }
    
    /*
     * (non-Javadoc)
     * @see sim.port.IPortFactory#createInPort()
     */
    @Override
    public <T> IInSimPort<T> createInPort() {
        return portFactory.createInPort();
    }
    
    /*
     * (non-Javadoc)
     * @see sim.port.IPortFactory#createOutPort()
     */
    @Override
    public <T> IOutSimPort<T> createOutPort() {
        return portFactory.createOutPort();
    }
    
    /*
     * (non-Javadoc)
     * @see sim.port.IPortFactory#createForwardPort()
     */
    @Override
    public <T> IForwardPort<T> createForwardPort() {
        return portFactory.createForwardPort();
    }
    
    /**
     * @param fact factory tu use
     */
    public void setPortFactory(IPortFactory fact) {
        this.portFactory = fact;
    }
}
