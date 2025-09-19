/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.port;

import montiarc.rte.msg.Message;

/**
 * A port that can send messages and thereby is the source of connectors.
 *
 * @param <T>
 */
public interface OutPort<T> extends Port, Sender<T> {

}
