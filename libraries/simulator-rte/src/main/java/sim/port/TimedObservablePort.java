/* (c) https://github.com/MontiCore/monticore */
/**
 * 
 */
package sim.port;

import sim.generic.Message;
import sim.generic.TickedMessage;

/**
 * A port that notifies its observers if data and tick messages are accepted.
 *
 *
 * @param <T> port data type
 */
public class TimedObservablePort<T> extends ObservablePort<T> {
    
    /*
     * (non-Javadoc)
     * @see sim.port.IInPort#accept(sim.generic.TickedMessage)
     */
    @Override
    public void accept(TickedMessage<? extends T> message) {
        setChanged();
        if (message.isTick()) {
            notifyObservers(message);            
        }
        else {
            @SuppressWarnings("unchecked")
            Message<T> data = (Message<T>) message;
            notifyObservers(data.getData());
        }
        clearChanged();
    }
    
    /*
     * (non-Javadoc)
     * @see sim.port.IInPort#accept(java.lang.Object)
     */
    @Override
    public void accept(T data) {
        setChanged();
        notifyObservers(data);
        clearChanged();
    }
}
