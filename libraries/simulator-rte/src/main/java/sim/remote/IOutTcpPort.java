/**
 * 
 */
package sim.remote;

import sim.port.IOutSimPort;

/**
 * 
 * Interface for an outgoing remote tcp port.
 *
 * @author  (last commit) $Author: ahaber $
 * @version $Revision: 2923 $,
 *          $Date: 2014-08-06 15:15:06 +0200 (Mi, 06 Aug 2014) $
 * @since   2.5.0
 *
 * @param <T> communication data type
 */
public interface IOutTcpPort<T> extends IOutSimPort<T> {
    
    public void addReceiver(String address, int tcpPort);
    
    
}
