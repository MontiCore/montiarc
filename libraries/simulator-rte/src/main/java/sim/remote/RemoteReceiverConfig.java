/* (c) https://github.com/MontiCore/monticore */
package sim.remote;

/**
 * 
 * TCP configuration.
 *
 * @author  (last commit) $Author: ahaber $
 * @version $Revision: 2923 $,
 *          $Date: 2014-08-06 15:15:06 +0200 (Mi, 06 Aug 2014) $
 * @since   2.5.0
 *
 */
public class RemoteReceiverConfig {

	private final String address;

	private final int tcpPort;

	public RemoteReceiverConfig(String a, int p) {
		address = a;
		tcpPort = p;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @return the tcpPort
	 */
	public int getTcpPort() {
		return tcpPort;
	}
}
