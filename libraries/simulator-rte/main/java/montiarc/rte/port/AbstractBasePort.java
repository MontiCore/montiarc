/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.port;

import montiarc.rte.msg.MessageFilteringStrategy;

/**
 * Abstract base class for all port types.
 *
 * @param <T> the type that can be sent/received via this port
 */
abstract class AbstractBasePort<T> implements MessageFilteringStrategy<T> {

  /**
   * The name of this port.
   */
  protected final String qualifiedName;

  protected AbstractBasePort(String qualifiedName) {
    this.qualifiedName = qualifiedName;
  }
}
