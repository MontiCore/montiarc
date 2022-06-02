/* (c) https://github.com/MontiCore/monticore */
package sim.remote;

import sim.port.IInSimPort;

/**
 * Interface for an incoming remote tcp port.
 *
 * @param <T> communication data type
 */
public interface IInTCPPort<T> extends IInSimPort<T> {

  void startListenOn(int tcpPort);
}
