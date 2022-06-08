/* (c) https://github.com/MontiCore/monticore */
package sim.automaton;

import sim.message.TickedMessage;
import sim.port.IPort;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ComponentState implements Serializable {

  private String componentName;

  private AutomataState currentState;

  private List<Serializable> stateVariables;

  private IPort inputPort;

  private TickedMessage inMessage;

  private long inMessageID;

  private Map<IPort, List<TickedMessage>> outMessages;
  private Map<IPort, List<Long>> outMessagesID;

  private String guard;

  private Boolean messageNotSend= false;


  public ComponentState(AutomataState currentState,
                        List<Serializable> stateVariables,
                        IPort inputPort,
                        TickedMessage inMessage,
                        Map<IPort, List<TickedMessage>> outMessages, String componentName, String guard) {
    this.currentState = currentState;
    this.stateVariables = stateVariables;
    this.inputPort = inputPort;
    this.inMessage = inMessage;
    this.outMessages = outMessages;
    this.componentName= componentName;
    this.guard=guard;
  }

  public AutomataState getCurrentState() {
    return currentState;
  }

  public List<Serializable> getStateVariables() {
    return stateVariables;
  }

  public long getInMessageID() {
    return inMessageID;
  }

  public Map<IPort, List<TickedMessage>> getOutMessages() {
    return outMessages;
  }

  public TickedMessage getInMessage() {
    return inMessage;
  }

  public Map<IPort, List<Long>> getOutMessagesID() {
    return outMessagesID;
  }

  public IPort getInputPort() {
    return inputPort;
  }

  public void setInMessageID(long inMessageID) {
    this.inMessageID = inMessageID;
  }

  public void setOutMessagesID(Map<IPort, List<Long>> outMessagesID) {
    this.outMessagesID = outMessagesID;
  }

  public String getComponentName() {
    return componentName;
  }

  public String getGuard() {
    return guard;
  }

  public Boolean getMessageNotSend() {
    return messageNotSend;
  }

  public void setMessageNotSend(Boolean messageNotSend) {
    this.messageNotSend = messageNotSend;
  }
}
