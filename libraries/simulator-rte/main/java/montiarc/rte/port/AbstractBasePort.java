/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.port;

import montiarc.rte.port.messages.MessageFilteringStrategy;

/**
 * Abstract base class for all port types.
 *
 * @param <DataType> the type that can be sent/received via this port
 */
abstract class AbstractBasePort<DataType> implements MessageFilteringStrategy<DataType> {
  
  /**
   * The name of this port.
   */
  protected final String qualifiedName;
  
  protected AbstractBasePort(String qualifiedName) {
    this.qualifiedName = qualifiedName;
  }
}