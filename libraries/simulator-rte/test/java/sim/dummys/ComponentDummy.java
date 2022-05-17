/* (c) https://github.com/MontiCore/monticore */
package sim.dummys;

import sim.sched.IScheduler;
import sim.comp.IComponent;
import sim.error.ISimulationErrorHandler;
import sim.comp.AComponent;
import sim.comp.ATimedComponent;
import sim.message.Message;
import sim.message.Tick;
import sim.port.IInPort;
import sim.port.IInSimPort;
import sim.port.IOutPort;
import sim.port.IPort;
import sim.port.Port;
import sim.port.TestPort;

import java.util.LinkedList;
import java.util.List;

/**
 *
 */
public class ComponentDummy extends ATimedComponent implements ComponentDummyPortInterface {

  protected IInPort<String> p1In;

  public List<Message<String>> p1InReceivedMessages;

  protected IInPort<String> p2In;

  public List<Message<String>> p2InReceivedMessages;

  protected IOutPort<String> pOut;

  public ComponentDummy() {
    p1InReceivedMessages = new LinkedList<Message<String>>();
    p2InReceivedMessages = new LinkedList<Message<String>>();
  }

  /**
   * @see IComponent#checkConstraints()
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

  /**
   * @see sim.dummys.ComponentDummyPortInterface#p1InMessageReceived(Message)
   */
  @Override
  public void p1InMessageReceived(Message<String> message) {
    p1InReceivedMessages.add(message);
    pOut.send(message);
  }

  /**
   * @see sim.dummys.ComponentDummyPortInterface#p2InMessageReceived(Message)
   */
  @Override
  public void p2InMessageReceived(Message<String> message) {
    p2InReceivedMessages.add(message);
  }

  /**
   * @see AComponent#handleTick()
   */
  @Override
  public void handleTick() {
    pOut.send(Tick.<String>get());
    incLocalTime();
  }

  /**
   * @see IComponent#setup(IScheduler, ISimulationErrorHandler)
   */
  @Override
  public void setup(IScheduler scheduler, ISimulationErrorHandler errorHandler) {
    p1In = new Port<String>();
    p2In = new Port<String>();
    pOut = new TestPort<String>();
    ((IInSimPort<String>) p1In).setup(this, scheduler);
    ((IInSimPort<String>) p2In).setup(this, scheduler);
  }

  @Override
  @SuppressWarnings("unchecked")
  public void handleMessage(IInPort<?> port, Message<?> message) {
    if (port == p1In) {
      p1InMessageReceived((Message<String>) message);
    } else if (port == p2In) {
      p2InMessageReceived((Message<String>) message);
    }
  }

  /**
   * @param dummyReceiver dummy receiver
   */
  public void setPout(IPort<String> dummyReceiver) {
    this.pOut = dummyReceiver;

  }

  /**
   * @see ATimedComponent#timeStep()
   */
  @Override
  protected void timeStep() {

  }
}