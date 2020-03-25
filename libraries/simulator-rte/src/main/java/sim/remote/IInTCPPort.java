/* (c) https://github.com/MontiCore/monticore */
/**
 * 
 */
package sim.remote;

import sim.port.IInSimPort;

/**
 * 
 * Interface for an incoming remote tcp port.
 *
 *          $Date: 2014-08-06 15:15:06 +0200 (Mi, 06 Aug 2014) $
 *
 * @param <T> communication data type
 */
public interface IInTCPPort<T> extends IInSimPort<T> {
    
    public void startListenOn(int tcpPort);
    
}
