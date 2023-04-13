/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.port;

import montiarc.rte.port.messages.PassAllMessageFilter;

/**
 * An outgoing port of a MontiArc component that can send ticks.
 *
 * @param <T> the type that can be sent via this port
 */
public class TimeAwareOutPort<T> extends AbstractOutPort<T> implements PassAllMessageFilter<T> {

  public TimeAwareOutPort(String qualifiedName) {
    super(qualifiedName);
  }
}
