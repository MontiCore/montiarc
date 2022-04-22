/* (c) https://github.com/MontiCore/monticore */
package sim.port;

import sim.message.Message;
import sim.message.TickedMessage;

/**
 * A port that notifies its observers if data and tick messages are accepted.
 *
 * @param <T> port data type
 */
public class TimedObservablePort<T> extends ObservablePort<T> {

  /**
   * @see IPort##accept(TickedMessage)
   */
  @Override
  public void accept(TickedMessage<? extends T> message) {
    setChanged();
    if (message.isTick()) {
      notifyObservers(message);
    } else {
      @SuppressWarnings("unchecked")
      Message<T> data = (Message<T>) message;
      notifyObservers(data.getData());
    }
    clearChanged();
  }

  /**
   * @see IPort##accept(T)
   */
  @Override
  public void accept(T data) {
    setChanged();
    notifyObservers(data);
    clearChanged();
  }
}
