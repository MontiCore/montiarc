/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.port;

import montiarc.rte.component.IComponent;
import montiarc.rte.msg.Message;
import montiarc.rte.msg.Tick;
import montiarc.rte.scheduling.TimeAwareScheduler;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Objects;


public class TimeAwarePortForComposition<T> extends TimeAwareOutPort<T> implements SyncAwareInPort<T> {

  protected Deque<Message<T>> buffer = new ArrayDeque<>();
  protected TimeAwareScheduler scheduler;

  public TimeAwarePortForComposition(String qualifiedName, IComponent owner) {
    super(qualifiedName, owner);
  }
  public TimeAwarePortForComposition(String qualifiedName, IComponent owner, TimeAwareScheduler scheduler) {
    this(qualifiedName, owner);
    this.scheduler = scheduler;
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
      this.processReceivedTick();
    } else {
      this.processReceivedData(message.getData());
    }
  }

  /**
   * Process a received data message on this port.
   * <br>
   * Do not call this method to <i>send</i> messages to this port, use {@link #receive(Message)} instead:
   * {@code receive(Message.of(data))}.
   *
   * @param data the received data
   */
  protected void processReceivedData(T data) {
    Message<T> msgObject = Message.of(data);
    if (messageIsValidOnPort(msgObject)) {
      buffer.add(msgObject);
      scheduler.requestScheduling(this, data);
    }
  }

  /**
   * Process a received tick message on this port.
   * Do not call this method to <i>send</i> messages to this port, use {@link #receive(Message)} instead:
   * {@code receive(Tick.get())}.
   */
  protected void processReceivedTick() {
    if (messageIsValidOnPort(Tick.get())) {
      buffer.add(Tick.get());
      scheduler.requestSchedulingOfNewTick(this);
    }
  }

  @Override
  public Message<T> peekBuffer() {
    return buffer.peek();
  }

  @Override
  public Message<T> peekLastBuffer(){
    return buffer.peekLast();
  }

  @Override
  public Message<T> pollBuffer() {
    return buffer.poll();
  }

  @Override
  public Message<T>pollLastBuffer(){
    return buffer.pollLast();
  }

  @Override
  public boolean isBufferEmpty() {
    return buffer.isEmpty();
  }

  @Override
  public boolean hasBufferedTick() {
    return buffer.contains(Tick.get());
  }

  public Deque<Message<T>> getBuffer() {
    return new ArrayDeque<>(this.buffer);
  }

  public void forward() {
    this.send(buffer.poll());
  }

  @Override
  public void dropMessagesIgnoredBySync() {
    Iterator<Message<T>> iterator = buffer.iterator();
    int msgsBeforeTick = 0;
    while (iterator.hasNext() && iterator.next() != Tick.get()) {
       msgsBeforeTick++;
    }

    // we drop all messages before the tick - 1 (the last one should remain)
    // If there was no tick, we drop all messages except for the last one
    for (int i = msgsBeforeTick; i > 1; i--) {
      buffer.poll();
    }
  }
}
