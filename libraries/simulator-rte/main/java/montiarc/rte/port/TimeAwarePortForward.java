/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.port;

import montiarc.rte.component.IComponent;
import montiarc.rte.msg.Message;
import montiarc.rte.msg.Tick;

/**
 * This class represents a port-forwarding connector in a MontiArc model (time-aware variant).
 * <br>
 * A port-forwarding connector is either going from a subcomponent to an outgoing port of
 * its owning component or from an incoming port of a component to an incoming port of
 * one of its subcomponents. If one incoming port is connected to multiple subcomponents,
 * the connection can no longer be represented by sharing the port instance, thus this structure is required.
 *
 * @param <T> the type that is sent via this forward
 */
public class TimeAwarePortForward<T> extends TimeAwareOutPort<T> implements ITimeAwareInPort<T> {

  public TimeAwarePortForward(String qualifiedName, IComponent owner) {
    super(qualifiedName, owner);
  }

  /**
   * Receive a message on this port, which is buffered for forwarding.
   * This method should only be called by the {@link IOutPort}
   * to which this port forward is connected.
   *
   * @param message the message sent by the connected outgoing port
   */
  @Override
  public void receive(Message<? extends T> message) {
    if (message == Tick.get()) {
      this.sendTick();
    } else {
      this.send(message.getData());
    }
  }

  
  /**
   * Peek the next message in the buffer.
   *
   * @return the next message in the buffer
   */
  @Override
  public Message<T> peekBuffer() {
    throw new UnsupportedOperationException("Forwarding port has no buffer");
  }
  
  /**
   * Port forwards' buffers should not be polled directly.
   * Therefore, this method throws an exception.
   * Rather, {@link ()} should be used to process messages.
   *
   * @return null
   */
  @Override
  public Message<T> pollBuffer() {
    throw new UnsupportedOperationException("Forwarding port has no buffer");
  }
  
  /**
   * Check whether the buffer is empty.
   *
   * @return true if the buffer is empty
   */
  @Override
  public boolean isBufferEmpty() {
    throw new UnsupportedOperationException("Forwarding port has no buffer");
  }
  
  /**
   * Check whether the buffer holds at least one tick.
   *
   * @return true if the buffer contains a tick
   */
  @Override
  public boolean hasBufferedTick() {
    throw new UnsupportedOperationException("Forwarding port has no buffer");
  }

  @Override
  public Message<T> peekLastBuffer() {
    throw new UnsupportedOperationException("Forwarding port has no buffer");
  }

  @Override
  public Message<T> pollLastBuffer() {
    throw new UnsupportedOperationException("Forwarding port has no buffer");
  }
}
