/*
 * Copyright (c) 2014 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package sim.sched;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
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
 * Simulation scheduler with a default strategy that uses a {@link IPortMap} to check, if ports are tickfree.
 * 
 * @author (last commit) $Author: ahaber $
 * @version $Revision: 3136 $, $Date: 2015-03-19 14:43:21 +0100 (Do, 19 Mrz 2015) $
 * @since 2.5.0
 */
class SimSchedulerPortMap implements IScheduler {
    
    /**
     * Maps each scheduled component to a list of scheduled ports.
     */
    private final Map<ISimComponent, List<IInSimPort<?>>> comp2SimPorts;
    
    /**
     * Maps each scheduled component to a {@link PortMap}.
     */
    private final Map<ISimComponent, IPortMap> comp2tickfree;
    
    /**
     * Flags true, if all components are initialized correctly. Is reset, if a new component is
     * registered via {@link #setupPort(IInSimPort, ISimComponent)}.
     */
    private boolean allInitialized;
    
    /**
     * Used to produce ports.
     */
    private IPortFactory portFactory;
    
    /**
     * 
     */
    protected SimSchedulerPortMap() {
        comp2tickfree = new HashMap<ISimComponent, IPortMap>();
        comp2SimPorts = new HashMap<ISimComponent, List<IInSimPort<?>>>();
        portFactory = new DefaultPortFactory();
        allInitialized = false;
        init();
    }
    
    /**
     * Organizes {@link #comp2tickfree} for all registered components.
     */
    private void checkComponentInit() {
        if (!allInitialized) {
            for (ISimComponent comp : comp2SimPorts.keySet()) {
                if (comp2tickfree.get(comp) == null) {
                    Set<IInSimPort<?>> unconnected = new HashSet<IInSimPort<?>>();
                    
                    List<IInSimPort<?>> portOfComp = comp2SimPorts.get(comp);
                    
                    for (IInSimPort<?> p : portOfComp) {
                        if (!p.isConnected()) {
                            unconnected.add(p);
                        }
                    }
                    if (unconnected.size() != portOfComp.size()) {
                        portOfComp.removeAll(unconnected);
                    }
                    comp2tickfree.put(comp, new PortMap(portOfComp));
                }
            }
            allInitialized = true;
        }
    }
    
    /* (non-Javadoc)
     * 
     * @see sim.IPortFactory#createIncomingForwardingPort() */
    @Override
    public <T> IForwardPort<T> createForwardPort() {
        return portFactory.createForwardPort();
    }
    
    /* (non-Javadoc)
     * 
     * @see sim.IPortFactory#createIncomingPort() */
    @Override
    public <T> IInSimPort<T> createInPort() {
        return portFactory.createInPort();
    }
    
    /* (non-Javadoc)
     * 
     * @see sim.port.IPortFactory#createOutPort() */
    @Override
    public <T> IOutSimPort<T> createOutPort() {
        return portFactory.createOutPort();
    }
    
    /**
     * @return comp2SimPorts
     */
    protected Map<ISimComponent, List<IInSimPort<?>>> getComp2SimPorts() {
        return this.comp2SimPorts;
    }
    
    /**
     * @return comp2tickfree
     */
    protected Map<ISimComponent, IPortMap> getComp2tickfree() {
        return this.comp2tickfree;
    }
    
    /**
     * @see sim.IScheduler#getPortFactory()
     */
    @Override
    public IPortFactory getPortFactory() {
        return this.portFactory;
    }
    
    /* (non-Javadoc)
     * 
     * @see sim.IScheduler#init() */
    @Override
    public void init() {
        comp2tickfree.clear();
    }
    
    /* (non-Javadoc)
     * 
     * @see sim.IScheduler#mapPortToComponent(sim.generic.IInSimPort) */
    @Override
    public void setupPort(IInSimPort<?> port) {
        ISimComponent component = port.getComponent();
        if (!comp2tickfree.containsKey(component)) {
            component.setSimulationId(comp2SimPorts.size());
            comp2tickfree.put(component, null);            
            comp2SimPorts.put(component, new LinkedList<IInSimPort<?>>());
            // we added a component that has not been initialized yet
            allInitialized = false;
        }
        comp2SimPorts.get(component).add(port);
    }
    
    /**
     * @param comp component of the given port
     * @param port port to schedule
     * @param msg msg to process
     * @return true, if msg is handled by port's component immediately, else false
     */
    protected boolean processData(ISimComponent comp, IInSimPort<?> port, Message<?> msg) {
        boolean success = false;
        IPortMap tickless = comp2tickfree.get(comp);
        
        if (tickless.isTickfree(port)) {
            success = true;
            comp.handleMessage(port, msg);
            comp.checkConstraints();
        }
        
        return success;
    }
    
    /**
     * @param comp component of the given port
     * @param port port to schedule
     * @return true, if msg is handled by port's component immediately, else false
     */
    protected boolean processTick(ISimComponent comp, IInSimPort<?> port) {
        boolean success = false;
        IPortMap tickfree = comp2tickfree.get(comp);
        
        tickfree.setPortBlocked(port);
        if (tickfree.allPortsBlocked()) {
            success = true;
            List<IInSimPort<?>> allPorts = comp2SimPorts.get(comp);
            comp.handleTick();
            // wake ports up
            for (IInSimPort<?> p : allPorts) {
                p.wakeUp();
            }
            
            // reorganize tickfree ports
            for (IInSimPort<?> p : allPorts) {
                if (!p.hasTickReceived()) {
                    tickfree.setPortTickfree(p);
                }
            }
            // trigger processing of buffered messages
            for (IInSimPort<?> p : allPorts) {
                p.processBufferedMsgs();
            }
        }
        
        return success;
    }
    
    /* (non-Javadoc)
     * 
     * @see sim.IScheduler#addPort(sim.generic.IIncomingPort, sim.generic.TickedMessage) */
    @Override
    public boolean registerPort(IInSimPort<?> port, TickedMessage<?> msg) {
        checkComponentInit();
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
    
    /* (non-Javadoc)
     * 
     * @see sim.IScheduler#setPortFactors(sim.port.IPortFactory) */
    @Override
    public void setPortFactory(IPortFactory fact) {
        this.portFactory = fact;
    }
    
}