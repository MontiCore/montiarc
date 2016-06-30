package sim.remote;

import sim.port.ForwardPort;

/**
 * 
 * Interface for a remote forwarding incoming tcp port.
 *
 * @author  (last commit) $Author: ahaber $
 * @version $Revision: 2972 $,
 *          $Date: 2014-08-31 16:58:17 +0200 (So, 31 Aug 2014) $
 * @since   2.5.0
 *
 * @param <T> communication data type
 */
public class ForwardingTCPPort<T> extends ForwardPort<T> implements IInTCPPort<T> {
	
	public void startListenOn(int tcpPort) {
		new Thread(new InPortTCPServer<T>(this, tcpPort)).start();
	}

}
