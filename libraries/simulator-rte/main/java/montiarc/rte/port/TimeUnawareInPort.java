/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.port;

import montiarc.rte.component.IComponent;
import montiarc.rte.msg.IgnoreTicksMessageFilter;

/**
 * An incoming port of a MontiArc component that cannot receive ticks.
 *
 * @param <T> the type that can be received via this port
 */
public class TimeUnawareInPort<T> extends AbstractInPort<T> implements IgnoreTicksMessageFilter<T> {

  public TimeUnawareInPort(String qualifiedName, IComponent<?, ?> owner) {
    super(qualifiedName, owner);
  }
}
