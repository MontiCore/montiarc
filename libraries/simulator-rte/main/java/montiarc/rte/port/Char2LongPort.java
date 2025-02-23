/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.port;

import montiarc.rte.component.Component;
import montiarc.rte.msg.Message;
import montiarc.rte.msg.Tick;

import java.util.Objects;

public class Char2LongPort extends AbstractOutPort<Long> implements InOutPort<Character, Long> {

  public Char2LongPort(String qualifiedName, Component owner) {
    super(qualifiedName, owner);
  }

  @Override
  public void receive(Message<? extends Character> message) {
    Objects.requireNonNull(message);
    // Directly forward the message
    if (Tick.get().equals(message)) {
      this.sendTick();
    } else {
      Objects.requireNonNull(message.getData());
      this.send((long) (message.getData().charValue()));
    }
  }

  @Override
  public Message<Character> peekBuffer() {
    /* Behave as if there was an empty buffer */
    return null;
  }

  @Override
  public Message<Character> pollBuffer() {
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
  public Message<Character> peekLastBuffer() {
    /* Behave as if there was an empty buffer */
    return null;
  }

  @Override
  public Message<Character> pollLastBuffer() {
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
