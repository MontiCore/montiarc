/**
 * 
 */
package sim.sched;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sim.IScheduler;
import sim.generic.ISimComponent;
import sim.generic.Message;
import sim.generic.TickedMessage;
import sim.port.DefaultPortFactoryDeprecated;
import sim.port.IForwardPort;
import sim.port.IInSimPort;
import sim.port.IOutSimPort;
import sim.port.IPortFactory;

/**
 * Old default simulation scheduler. 
 * Use {@link SchedulerFactory#createDefaultScheduler()} instead. DefaultPortFactoryDeprecated is for tests only.
 * <br>
 * <br>
 * Copyright (c) 2011 RWTH Aachen. All rights reserved.
 * 
 * @author (last commit) $LastChangedBy: ahaber $
 * @version $LastChangedDate: 2014-10-14 10:42:14 +0200 (Di, 14 Okt 2014) $<br>
 *          $LastChangedRevision: 3036 $
 *          
 * 
 */
public class DefaultSimSchedulerDeprecated implements IScheduler {
    
    /**
     * Used to produce ports.
     */
    protected IPortFactory portFactory;
    
    /**
     * Tickless set for every component that is scheduled by this scheduler.
     */
    protected Map<ISimComponent, Set<IInSimPort<?>>> comp2tickless;
    
    /**
     * Maps a component to its incoming ports.
     */
    protected Map<ISimComponent, List<IInSimPort<?>>> comp2Ports;
    
    /**
     * Contains currently processed ports to prevent self activation in feedback cycles. 
     */
    protected Set<IInSimPort<?>> activePorts;
    
    /**
     * 
     */
    public DefaultSimSchedulerDeprecated() {
        init();
        portFactory = new DefaultPortFactoryDeprecated();
        activePorts = new HashSet<IInSimPort<?>>();
    }
    
    /*
     * (non-Javadoc)
     * @see sim.IPortFactory#createIncomingPort()
     */
    @Override
    public <T> IInSimPort<T> createInPort() {
        return portFactory.createInPort();
    }
    
    /*
     * (non-Javadoc)
     * @see sim.IPortFactory#createIncomingForwardingPort()
     */
    @Override
    public <T> IForwardPort<T> createForwardPort() {
        return portFactory.createForwardPort();
    }
    
    /*
     * (non-Javadoc)
     * @see sim.IScheduler#addPort(sim.generic.IIncomingPort,
     * sim.generic.TickedMessage)
     */
    @Override
    public boolean registerPort(IInSimPort<?> port, TickedMessage<?> msg) {
        boolean success = false;
        if (!activePorts.contains(port)) {
            activePorts.add(port);
            ISimComponent comp = port.getComponent();
            if (msg.isTick()) {
                success = processTick(comp, port);
            }
            else {
                success = processData(comp, port, (Message<?>) msg);
            }
            activePorts.remove(port);
        }

        return success;
        
    }
    
    /**
     * @param comp component of the given port
     * @param port port to schedule
     * 
     * @return true, if msg is handled by port's component immediately, else
     *         false 
     */
    protected boolean processTick(ISimComponent comp, IInSimPort<?> port) {
        Set<IInSimPort<?>> tickless = comp2tickless.get(comp);
        boolean success = tickless.remove(port);
        
        if (success && tickless.isEmpty()) {
            comp.handleTick();
            // wake ports up
            for (IInSimPort<?> p : comp2Ports.get(comp)) {
                if (!p.hasTickReceived()) {
                    tickless.add(p);                                  
                }
            }
            for (IInSimPort<?> p : comp2Ports.get(comp)) {
                p.wakeUp();
            }
        }
        else {
            success = false;
        }
        
        return success;
    }

    /**
     * @param comp component of the given port
     * @param port port to schedule
     * @param msg msg to process
     * 
     * @return true, if msg is handled by port's component immediately, else
     *         false
     */
    protected boolean processData(ISimComponent comp, IInSimPort<?> port, Message<?> msg) {
        boolean success = false;
        Set<IInSimPort<?>> tickless = comp2tickless.get(comp);
        if (tickless.contains(port)) {
            success = true;
            comp.handleMessage(port, msg);
            
        }

        return success;
    }
    
    /* (non-Javadoc)
     * 
     * @see sim.IScheduler#mapPortToComponent(sim.generic.IInSimPort) */
    @Override
    public void setupPort(IInSimPort<?> port) {
        ISimComponent component = port.getComponent();
        if (!comp2tickless.containsKey(component)) {
            comp2tickless.put(component, new HashSet<IInSimPort<?>>());
        }
        
        if (!comp2Ports.containsKey(component)) {
            comp2Ports.put(component, new ArrayList<IInSimPort<?>>());
        }
        comp2tickless.get(component).add(port);
        comp2Ports.get(component).add(port);
    }
    
    /*
     * (non-Javadoc)
     * @see sim.IScheduler#init()
     */
    @Override
    public void init() {
        comp2tickless = new HashMap<ISimComponent, Set<IInSimPort<?>>>();
        comp2Ports = new HashMap<ISimComponent, List<IInSimPort<?>>>();
    }
    
    /*
     * (non-Javadoc)
     * @see sim.IScheduler#setPortFactors(sim.port.IPortFactory)
     */
    @Override
    public void setPortFactory(IPortFactory fact) {
        this.portFactory = fact;
    }

    /* (non-Javadoc)
     * @see sim.port.IPortFactory#createOutPort()
     */
    @Override
    public <T> IOutSimPort<T> createOutPort() {
        return portFactory.createOutPort();
    }

    /**
     * @see sim.IScheduler#getPortFactory()
     */
    @Override
    public IPortFactory getPortFactory() {
        return portFactory;
    }
    
    
}
