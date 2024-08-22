/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.port;

import montiarc.rte.component.Component;
import montiarc.rte.msg.Message;
import montiarc.rte.msg.Tick;

/**
 * This class represents a port-forwarding connector in a MontiArc model.
 * <br>
 * If it receives a message, it forwards it to the connected ports. No message is buffered.
 * Therefore, this port won't participate in any scheduling either. It should be used as outgoing port
 * in a decomposition structure. Thereby, it receives messages from the inner structure and forwards them
 * to the outside.
 *
 * @param <T> the type that is sent via this forward
 */
public class PortForward<T> extends AbstractOutPort<T> implements InOutPort<T> {

  public PortForward(String qualifiedName, Component owner) {
    super(qualifiedName, owner);
  }

  @Override
  public void receive(Message<? extends T> message) {
    // Directly forward the message
    if (message == Tick.get()) {
      this.sendTick();
    } else {
      this.send(message.getData());
    }
  }

  @Override
  public Message<T> peekBuffer() {
    /* Behave, if there was an empty buffer */
    return null;
  }

  @Override
  public Message<T> pollBuffer() {
    /* Behave, if there was an empty buffer */
    return null;
  }

  @Override
  public boolean isBufferEmpty() {
    /* Behave, if there was an empty buffer */
    return true;
  }

  @Override
  public boolean hasBufferedTick() {
    /* Behave, if there was an empty buffer */
    return false;
  }

  @Override
  public Message<T> peekLastBuffer() {
    /* Behave, if there was an empty buffer */
    return null;
  }

  @Override
  public Message<T> pollLastBuffer() {
    /* Behave, if there was an empty buffer */
    return null;
  }

  @Override
  public void dropMessagesIgnoredBySync() {
    /* Behave, if there was an empty buffer */
    // Do nothing
  }

  @Override
  public void forwardWithoutRemoval() {
    /* Behave, if there was an empty buffer */
    // Do nothing
  }
}
