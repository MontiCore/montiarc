/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.port;

import montiarc.rte.component.Component;
import montiarc.rte.msg.Message;
import montiarc.rte.msg.Tick;

public class Number2BytePort<I extends Number> extends AbstractOutPort<Byte> implements InOutPort<I, Byte> {

  public Number2BytePort(String qualifiedName, Component owner) {
    super(qualifiedName, owner);
  }

  @Override
  public void receive(Message<? extends I> message) {
    // Directly forward the message
    if (Tick.get().equals(message)) {
      this.sendTick();
    } else {
      this.send(message.getData().byteValue());
    }
  }

  @Override
  public Message<I> peekBuffer() {
    /* Behave as if there was an empty buffer */
    return null;
  }

  @Override
  public Message<I> pollBuffer() {
    /* Behave as if there was an empty buffer */
    return null;
  }

  @Override
  public boolean isBufferEmpty() {
    /* Behave as if there was an empty buffer */
    return true;
  }

  @Override
  public boolean hasBufferedTick() {
    /* Behave as if there was an empty buffer */
    return false;
  }

  @Override
  public Message<I> peekLastBuffer() {
    /* Behave as if there was an empty buffer */
    return null;
  }

  @Override
  public Message<I> pollLastBuffer() {
    /* Behave as if there was an empty buffer */
    return null;
  }

  @Override
  public void dropMessagesIgnoredBySync() {
    /* Behave as if there was an empty buffer */
  }

  @Override
  public void forwardWithoutRemoval() {
    /* Behave as if there was an empty buffer */
  }
}
