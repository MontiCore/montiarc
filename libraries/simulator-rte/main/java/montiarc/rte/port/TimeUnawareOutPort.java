/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.port;

import montiarc.rte.component.IComponent;
import montiarc.rte.msg.IgnoreTicksMessageFilter;

/**
 * An outgoing port of a MontiArc component that cannot send ticks.
 *
 * @param <T> the type that can be sent via this port
 */
public class TimeUnawareOutPort<T> extends AbstractOutPort<T> implements IgnoreTicksMessageFilter<T> {

  public TimeUnawareOutPort(String qualifiedName, IComponent<?, ?> owner) {
    super(qualifiedName, owner);
  }
}
