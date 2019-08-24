/* (c) https://github.com/MontiCore/monticore */
package sim.port;


/**
 * Is used to encapsulate the forwarded ports from inner components in one
 * architecture component port. Is used like a regular IIncomingPort
 * 
 * 
 * @see IInPort
 * @param <T> data type of this port
 */
public interface IForwardPort<T> extends IPort<T> {
    
    /**
     * Adds the given incoming port to this encapsulating architecture port.
     * 
     * @param port port to encapsulate
     */
    void add(IInSimPort<? super T> port);
    
    /**
     * Removes the given port from this encapsulating architecture port.
     * 
     * @param port port to remove
     * @return true, if removal was successful, else false
     */
    boolean removeEncapsulatedPort(IInSimPort<? super T> port);
}
