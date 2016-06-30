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
import sim.port.DefaultPortFactory;
import sim.port.IForwardPort;
import sim.port.IInSimPort;
import sim.port.IOutSimPort;
import sim.port.IPortFactory;

/**
 * Default simulation scheduler. <br>
 * <br>
 * Copyright (c) 2011 RWTH Aachen. All rights reserved.
 * 
 * @author (last commit) $LastChangedBy: ahaber $
 * @version $LastChangedDate: 2015-03-19 14:43:21 +0100 (Do, 19 Mrz 2015) $<br>
 *          $LastChangedRevision: 3136 $
 * 
 * @deprecated Will be removed after 2.5.0 release. Use {@link SchedulerFactory#createDefaultScheduler()} instead.
 */
public class SimSchedulerHashSet implements IScheduler {
    
    /**
     * Maps a component to its incoming ports.
     */
    private final Map<ISimComponent, List<IInSimPort<?>>> comp2Ports;
    
    /**
     * Set of tick-free ports (not received a tick yet) for every scheduled component.
     */
    private final Map<ISimComponent, Set<IInSimPort<?>>> comp2tickfree;

    /**
     * Flags true, if registered ports haven been checked.
     */
    private boolean isInitialized;
    
    /**
     * Used to produce ports.
     */
    private IPortFactory portFactory;

    /**
     * @deprecated Will be removed after 2.5.0 release. Use {@link SchedulerFactory#createDefaultScheduler()} instead.
     */
    public SimSchedulerHashSet() {
        comp2tickfree = new HashMap<ISimComponent, Set<IInSimPort<?>>>();
        comp2Ports = new HashMap<ISimComponent, List<IInSimPort<?>>>();
        portFactory = new DefaultPortFactory();
        isInitialized = false;
        init();
    }
    
    /**
     * Removes unconnected ports.
     */
    protected void checkRegisteredPorts() {
        for (ISimComponent comp : comp2Ports.keySet()) {
            Set<IInSimPort<?>> unconnected = new HashSet<IInSimPort<?>>();
            List<IInSimPort<?>> portOfComp = comp2Ports.get(comp);
            
            for (IInSimPort<?> p : portOfComp) {
                if (!p.isConnected()) {
                    unconnected.add(p);
                }
            }
            if (unconnected.size() != portOfComp.size()) {
                portOfComp.removeAll(unconnected);
                comp2tickfree.get(comp).removeAll(unconnected);
            }
        }
        isInitialized = true;
        
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
     * @see sim.IPortFactory#createIncomingPort()
     */
    @Override
    public <T> IInSimPort<T> createInPort() {
        return portFactory.createInPort();
    }
    
    /* (non-Javadoc)
     * @see sim.port.IPortFactory#createOutPort()
     */
    @Override
    public <T> IOutSimPort<T> createOutPort() {
        return portFactory.createOutPort();
    }
    
    /**
     * @return the comp2Ports
     */
    protected Map<ISimComponent, List<IInSimPort<?>>> getComp2Ports() {
        return comp2Ports;
    }
    
    /**
     * @return the comp2tickfree
     */
    protected Map<ISimComponent, Set<IInSimPort<?>>> getComp2tickfree() {
        return comp2tickfree;
    }

    /*
     * (non-Javadoc)
     * @see sim.IScheduler#init()
     */
    @Override
    public void init() {
        comp2tickfree.clear();
        comp2Ports.clear();
    }

    /* (non-Javadoc)
     * 
     * @see sim.IScheduler#mapPortToComponent(sim.generic.IInSimPort) */
    @Override
    public void setupPort(IInSimPort<?> port) {
        ISimComponent component = port.getComponent();
        if (!comp2tickfree.containsKey(component)) {
            comp2tickfree.put(component, new HashSet<IInSimPort<?>>());
        }
        
        if (!comp2Ports.containsKey(component)) {
            comp2Ports.put(component, new ArrayList<IInSimPort<?>>());
        }
        comp2tickfree.get(component).add(port);
        comp2Ports.get(component).add(port);
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
        Set<IInSimPort<?>> tickless = comp2tickfree.get(comp);
        if (tickless.contains(port)) {
            success = true;
            comp.handleMessage(port, msg);
            comp.checkConstraints();
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
        Set<IInSimPort<?>> tickfree = comp2tickfree.get(comp);
        
        boolean success = tickfree.remove(port);
        
        if (success && tickfree.isEmpty()) {
            List<IInSimPort<?>> allPorts = comp2Ports.get(comp);
            comp.handleTick();
            // wake ports up
            for (IInSimPort<?> p : allPorts) {
                p.wakeUp();
            }

            // reorganize tickfree ports
            for (IInSimPort<?> p : allPorts) {
                if (!p.hasTickReceived()) {
                    tickfree.add(p);                                  
                }
            }
            // trigger processing of buffered messages
            for (IInSimPort<?> p : allPorts) {
                p.processBufferedMsgs();
            }
        }
        else {
            success = false;
        }
        
        return success;
    }
    
    /*
     * (non-Javadoc)
     * @see sim.IScheduler#addPort(sim.generic.IIncomingPort,
     * sim.generic.TickedMessage)
     */
    @Override
    public boolean registerPort(IInSimPort<?> port, TickedMessage<?> msg) {
        if (!isInitialized) {
            checkRegisteredPorts();
        }
        boolean success = false;
        ISimComponent comp = port.getComponent();
        if (msg.isTick()) {
            success = processTick(comp, port);
        }
        else {
            success = processData(comp, port, (Message<?>) msg);
        }
        
        return success;
        
    }

    /*
     * (non-Javadoc)
     * @see sim.IScheduler#setPortFactors(sim.port.IPortFactory)
     */
    @Override
    public void setPortFactory(IPortFactory fact) {
        this.portFactory = fact;
    }

    /**
     * @see sim.IScheduler#getPortFactory()
     */
    @Override
    public IPortFactory getPortFactory() {
        return this.portFactory;
    }
    
    
}
