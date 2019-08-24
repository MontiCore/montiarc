/* (c) https://github.com/MontiCore/monticore */
package sim.generic;

/**
 * Messages with an associated priority. 
 * 
 * 
 * @param <T> data type
 */
public class PrioMessage<T> extends Message<T> {
    
    /**
     * Default priority.
     */
    public static final int DEFAULT_PRIORITY = 500;
    
    /**
     * Maximal priority.
     */
    public static final int MAX_PRIORITY = 1000;
    
    /**
     * Minimal priority.
     */
    public static final int MIN_PRIORITY = 0;
    
    /**
     * 
     */
    private static final long serialVersionUID = 26001153891410620L;
    
    /**
     * Priority of this message.
     */
    protected int priority;
    
    /**
     * Creates a {@link PrioMessage} with DEFAULT_PRIORITY.
     * 
     * @param data message data
     */
    public PrioMessage(final T data) {
        this(data, DEFAULT_PRIORITY);
    }
    
    /**
     * Creates a {@link PrioMessage} with the given priority.
     * 
     * @param data message data
     * @param prio priority of the message.
     */
    public PrioMessage(final T data, final int prio) {
        super(data);
        if (prio < MIN_PRIORITY) {
            this.priority = MIN_PRIORITY;
        }
        else if (prio > MAX_PRIORITY) {
            this.priority = MAX_PRIORITY;
        }
        else {
            this.priority = prio;
        }
    }
    
    /**
     * @return the priority
     */
    public int getPriority() {
        return priority;
    }   
    
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof PrioMessage) {
            PrioMessage<?> casted = (PrioMessage<?>) o;
            if (casted.getPriority() == this.getPriority() && casted.getData().equals(this.getData())) {
                result = true;
            }
        }
        return result;
    }
    
    @Override
    public String toString() {
        return this.data.toString() + "(" + this.getPriority() + ")";
    }

    /* (non-Javadoc)
     * @see sim.generic.Message#hashCode()
     */
    @Override
    public int hashCode() {
        return getData().hashCode() + getPriority();
    }
}
