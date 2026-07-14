/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.port;

import montiarc.rte.component.Component;
import montiarc.rte.msg.Message;
import montiarc.rte.msg.Tick;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class may observe an {@link OutPort} and provides access to its
 * observations. The observations are stored in order they here received.
 *
 * @param <T> the type of messages that are observed
 */
public class PortObserver<T> implements InPort<T> {

  protected Deque<Message<T>> observations = new ArrayDeque<>();

  @Override
  public void receive(Message<? extends T> message) {
    if (message == Tick.get()) {
      observations.add(Tick.get());
    } else {
      observations.add(Message.of(message.getData()));
    }
  }

  /**
   * @return an immutable list of messages in the order they were observed.
   *   Already polled messages are not included.
   */
  public List<Message<? extends T>> getObservedMessages() {
    return List.copyOf(observations);
  }

  /**
   * @return an immutable list of the messages' contents in order the messages
   *   were observed. Already polled messages are not included.
   */
  public List<T> getObservedValues() {
    return observations.stream()
      .filter(v -> !v.equals(Tick.get()))
      .map(Message::getData)
      .collect(Collectors.toList());
  }

  @Override
  public Message<T> peekBuffer() {
    return observations.peek();
  }

  @Override
  public Message<T> peekLastBuffer(){
    return observations.peekLast();
  }

  @Override
  public Message<T> pollBuffer() {
    return observations.poll();
  }

  @Override
  public Message<T> pollLastBuffer(){
    return observations.pollLast();
  }

  @Override
  public boolean isBufferEmpty() {
    return observations.isEmpty();
  }

  @Override
  public boolean hasBufferedTick() {
    return observations.contains(Tick.get());
  }

  /**
   * Drop all messages except for the last one that is queued before each tick.
   * If there is no tick queued, all messages except for the last one are dropped.
   */
  @Override
  public void dropMessagesIgnoredBySync() {
    var newObservations = new ArrayDeque<Message<T>>();
    var observationList = List.copyOf(this.observations);
    for (int i = 0; i < observationList.size() - 1; i++) {
      if (observationList.get(i) == Tick.get()
        || observationList.get(i+1) == Tick.get()) {
        newObservations.add(observationList.get(i));
      }
    }
    if (!newObservations.isEmpty()){
      newObservations.add(observationList.getLast());
    }
    this.observations = newObservations;
  }

  @Override
  public String getQualifiedName() {
    return null;
  }

  @Override
  public Component getOwner() {
    return null;
  }
}
