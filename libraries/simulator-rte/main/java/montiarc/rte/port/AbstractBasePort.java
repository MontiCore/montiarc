/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.port;

import montiarc.rte.component.IComponent;
import montiarc.rte.msg.MessageFilteringStrategy;

/**
 * Abstract base class for all port types.
 *
 * @param <T> the type that can be sent/received via this port
 */
abstract class AbstractBasePort<T> implements IPort, MessageFilteringStrategy<T> {

  /**
   * The name of this port.
   */
  protected final String qualifiedName;
  
  @Override
  public String getQualifiedName() {
    return qualifiedName;
  }

  protected final IComponent owner;

  protected AbstractBasePort(String qualifiedName, IComponent owner) {
    this.qualifiedName = qualifiedName;
    this.owner = owner;
  }

  @Override
  public IComponent getOwner() {
    return this.owner;
  }
}
