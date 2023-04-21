/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.port;

import montiarc.rte.msg.IgnoreTicksMessageFilter;

/**
 * An incoming port of a MontiArc component that cannot receive ticks.
 *
 * @param <T> the type that can be received via this port
 */
public abstract class TimeUnawareInPort<T> extends AbstractInPort<T> implements IgnoreTicksMessageFilter<T> {

  public TimeUnawareInPort(String qualifiedName) {
    super(qualifiedName);
  }
}
