/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.automaton;

import montiarc.rte.port.IInPort;
import montiarc.rte.port.messages.Message;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ComponentState<DataType> implements Serializable {

  private String componentName;

  private AutomataState currentState;

  private List<Serializable> stateVariables;

  private IInPort<DataType> inputPort;

  private Message<DataType> inMessage;

  private long inMessageID;

  private Map<IInPort<DataType>, List<Message<DataType>>> outMessages;
  private Map<IInPort<DataType>, List<Long>> outMessagesID;

  private String guard;

  private Boolean messageNotSend= false;


  public ComponentState(AutomataState currentState,
                        List<Serializable> stateVariables,
                        IInPort<DataType> inputPort,
                        Message<DataType> inMessage,
                        Map<IInPort<DataType>, List<Message<DataType>>> outMessages, String componentName, String guard) {
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

  public Map<IInPort<DataType>, List<Message<DataType>>> getOutMessages() {
    return outMessages;
  }

  public Message<DataType> getInMessage() {
    return inMessage;
  }

  public Map<IInPort<DataType>, List<Long>> getOutMessagesID() {
    return outMessagesID;
  }

  public IInPort<DataType> getInputPort() {
    return inputPort;
  }

  public void setInMessageID(long inMessageID) {
    this.inMessageID = inMessageID;
  }

  public void setOutMessagesID(Map<IInPort<DataType>, List<Long>> outMessagesID) {
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