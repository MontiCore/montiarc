/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.port;

import montiarc.rte.component.Component;
import montiarc.rte.msg.Message;
import montiarc.rte.msg.Tick;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class may observe an {@link OutPort} and provides access to its
 * observations. The observations are stored in order they here received.
 *
 * @param <T> the type of messages that are observed
 */
public class PortObserver<T> implements InPort<T> {

  protected List<Message<? extends T>> observations;

  public PortObserver() {
    this(new ArrayList<>());
  }

  protected PortObserver(ArrayList<Message<? extends T>> observations) {
    this.observations = observations;
  }

  @Override
  public void receive(Message<? extends T> message) {
    this.observations.add(message);
  }

  /**
   * @return an immutable list of messages in the order they were observed
   */
  public List<Message<? extends T>> getObservedMessages() {
    return List.copyOf(observations);
  }

  /**
   * @return an immutable list of the messages' contents in order the messages
   * were observed
   */
  public List<T> getObservedValues() {
    return observations.stream()
      .filter(v -> !v.equals(Tick.get()))
      .map(Message::getData)
      .collect(Collectors.toList());
  }

  @Override
  public Message<T> peekBuffer() {
    return null;
  }

  @Override
  public Message<T> pollBuffer() {
    return null;
  }

  @Override
  public Message<T> peekLastBuffer() {
    return null;
  }

  @Override
  public Message<T> pollLastBuffer() {
    return null;
  }

  @Override
  public boolean isBufferEmpty() {
    return false;
  }

  @Override
  public boolean hasBufferedTick() {
    return false;
  }

  @Override
  public void dropMessagesIgnoredBySync() {

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
