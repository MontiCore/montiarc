/* (c) https://github.com/MontiCore/monticore */
package sim.port;

import sim.message.TickedMessage;

import java.util.LinkedList;
import java.util.Queue;

public class RoundRobinPort<T> extends Port<T> {

  private Queue<TickedMessage<?>> messageQueue;

  public RoundRobinPort() {
    super();
    messageQueue = new LinkedList<>();
  }

  @Override
  public void accept(TickedMessage<? extends T> message) {
    messageQueue.offer(message);

  }

  @Override
  public void processMessageQueue() {
    if (!messageQueue.isEmpty())
      super.accept((TickedMessage<? extends T>) messageQueue.poll());
  }

  @Override
  public Queue<TickedMessage<?>> getMessageQueue() {
    return messageQueue;
  }

  @Override
  public void setMessageQueue(Queue<TickedMessage<?>> messageQueue) {
    this.messageQueue = messageQueue;
  }
}
