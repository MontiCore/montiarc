/* (c) https://github.com/MontiCore/monticore */
package sim.sched;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
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
 * Default simulation scheduler for MontiArc simulations.
 * 
 */
public class SimSchedulerBoolArrayNoMaps implements IScheduler {
    
    private final List<List<IInSimPort<?>>> comp2SimPorts;
    
    /**
     * Set of tick-free ports (not received a tick yet) for every scheduled component.
     */
    private final List<boolean[]> comp2tickfree;
    
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
    public SimSchedulerBoolArrayNoMaps() {
        comp2tickfree = new ArrayList<boolean[]>();
        comp2SimPorts = new ArrayList<List<IInSimPort<?>>>();
        portFactory = new DefaultPortFactory();
        allInitialized = false;
        init();
    }
    
    /**
     * Removes unconnected ports from the given components scheduler and marks the component as initialized.
     * 
     * @param component to initialize
     */
    private void checkComponentInit() {
        if (!allInitialized) {
            for (int compId = 0; compId < comp2SimPorts.size(); compId++) {
                if (comp2tickfree.get(compId) == null) {
                    List<IInSimPort<?>> portOfComp = comp2SimPorts.get(compId);
                    Set<IInSimPort<?>> unconnected = new HashSet<IInSimPort<?>>();
                    
                    for (IInSimPort<?> p : portOfComp) {
                        if (!p.isConnected()) {
                            unconnected.add(p);
                        }
                    }
                    if (unconnected.size() != portOfComp.size()) {
                        portOfComp.removeAll(unconnected);
                    }
                    
                    int size = portOfComp.size();
                    boolean[] tickless = new boolean[size];
                    for (int i = 0; i < size; i++) {
                        portOfComp.get(i).setPortNumber(i);
                        tickless[i] = true;
                    }
                    comp2tickfree.set(compId, tickless);
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
    protected List<List<IInSimPort<?>>> getComp2SimPorts() {
        return this.comp2SimPorts;
    }
    
    /**
     * @return comp2tickfree
     */
    protected List<boolean[]> getComp2tickfree() {
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
        if (component.getSimulationId() < 0) {
            component.setSimulationId(comp2SimPorts.size());
            comp2SimPorts.add(new LinkedList<IInSimPort<?>>());
            comp2tickfree.add(null);
            // we added a component that has not been initialized yet
            allInitialized = false;
        }
        comp2SimPorts.get(component.getSimulationId()).add(port);
    }
    
    /**
     * @param comp component of the given port
     * @param port port to schedule
     * @param msg msg to process
     * @return true, if msg is handled by port's component immediately, else false
     */
    protected boolean processData(ISimComponent comp, IInSimPort<?> port, Message<?> msg) {
        boolean success = false;
        boolean[] tickless = comp2tickfree.get(comp.getSimulationId());
        
        if (tickless[port.getPortNumber()]) {
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
        int id = comp.getSimulationId();
        boolean[] tickfree = comp2tickfree.get(id);
        
        boolean success = tickfreeRemove(tickfree, port);
        
        if (success && tickfreeIsEmpty(tickfree)) {
            List<IInSimPort<?>> allPorts = comp2SimPorts.get(id);
            comp.handleTick();
            // wake ports up
            for (IInSimPort<?> p : allPorts) {
                p.wakeUp();
            }
            
            // reorganize tickfree ports
            for (IInSimPort<?> p : allPorts) {
                if (!p.hasTickReceived()) {
                    tickfree[p.getPortNumber()] = true;
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
    
    /**
     * @param tickfree
     * @return true, if all ports represented by tickfree have received a tick.
     */
    protected boolean tickfreeIsEmpty(boolean[] tickfree) {
        boolean result = true;
        for (boolean b : tickfree) {
            if (b) {
                return false;
            }
        }
        return result;
    }
    
    /**
     * Checks, if the given port is tickfree now. If so, it is set to be blocked by a tick.
     * 
     * @param tickfree tickfree storage
     * @param port port to check
     * @return true, if the given port has not been blocked by a tick before.
     */
    protected boolean tickfreeRemove(boolean[] tickfree, IInSimPort<?> port) {
        boolean success = false;
        int nr = port.getPortNumber();
        if (tickfree[nr]) {
            tickfree[nr] = false;
            success = true;
        }
        return success;
    }
}
