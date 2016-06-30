package sim.dummys;

import java.util.LinkedList;
import java.util.List;

import sim.IScheduler;
import sim.error.ISimulationErrorHandler;
import sim.generic.ATimedComponent;
import sim.generic.Message;
import sim.generic.Tick;
import sim.port.IInPort;
import sim.port.IInSimPort;
import sim.port.IOutPort;
import sim.port.IPort;
import sim.port.SimplePort;
import sim.port.TestPort;

/**
 * @author Arne Haber
 * @version 27.11.2008
 */
public class ComponentDummy extends ATimedComponent implements ComponentDummyPortInterface {
    
    protected IInPort<String> p1In;
    
    public List<Message<String>> p1InReceivedMessages;
    
    protected IInPort<String> p2In;
    
    public List<Message<String>> p2InReceivedMessages;
    
    protected IOutPort<String> pOut;
    
    /**
     * 
     */
    public ComponentDummy() {
        p1InReceivedMessages = new LinkedList<Message<String>>();
        p2InReceivedMessages = new LinkedList<Message<String>>();
    }
    
    /*
     * (non-Javadoc)
     * @see sim.generic.IComponent#checkConstraints()
     */
    @Override
    public void checkConstraints() {
        // TODO Auto-generated method stub
    }
    
    /**
     * @return the p1In
     */
    public IInPort<String> getP1In() {
        return p1In;
    }
    
    /**
     * @return the p2In
     */
    public IInPort<String> getP2In() {
        return p2In;
    }
    
    /**
     * @return the pOut
     */
    public IOutPort<String> getPOut() {
        return pOut;
    }
    
    /*
     * (non-Javadoc)
     * @see
     * sim.dummys.ComponentDummyPortInterface#p1InMessageReceived(sim.generic.
     * Message)
     */
    @Override
    public void p1InMessageReceived(Message<String> message) {
        p1InReceivedMessages.add(message);
        pOut.send(message);
    }
    
    /*
     * (non-Javadoc)
     * @see
     * sim.dummys.ComponentDummyPortInterface#p2InMessageReceived(sim.generic.
     * Message)
     */
    @Override
    public void p2InMessageReceived(Message<String> message) {
        p2InReceivedMessages.add(message);
    }
    
    /*
     * (non-Javadoc)
     * @see sim.generic.AComponent#sendTick()
     */
    @Override
    public void handleTick() {
        pOut.send(Tick.<String>get());
        incLocalTime();
    }
    
    /*
     * (non-Javadoc)
     * @see sim.generic.IComponent#setup(sim.IScheduler)
     */
    @Override
    public void setup(IScheduler scheduler, ISimulationErrorHandler errorHandler) {
        p1In = new SimplePort<String>();
        p2In = new SimplePort<String>();
        pOut = new TestPort<String>();
        ((IInSimPort<String>) p1In).setup(this, scheduler);
        ((IInSimPort<String>) p2In).setup(this, scheduler);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void handleMessage(IInPort<?> port, Message<?> message) {
        if (port == p1In) {
            p1InMessageReceived((Message<String>) message);
        }
        else if (port == p2In) {
            p2InMessageReceived((Message<String>) message);
        }
    }
    
    /**
     * @param dummyReceiver dummy receiver
     */
    public void setPout(IPort<String> dummyReceiver) {
        this.pOut = dummyReceiver;
        
    }

    /* (non-Javadoc)
     * @see sim.generic.AComponent#timeStep()
     */
    @Override
    protected void timeStep() {
        
    }
    
}
