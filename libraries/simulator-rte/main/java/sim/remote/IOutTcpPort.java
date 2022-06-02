/* (c) https://github.com/MontiCore/monticore */
package sim.remote;

import sim.port.IOutSimPort;

/**
 * Interface for an outgoing remote tcp port.
 *
 * @param <T> communication data type
 */
public interface IOutTcpPort<T> extends IOutSimPort<T> {

  void addReceiver(String address, int tcpPort);
}
