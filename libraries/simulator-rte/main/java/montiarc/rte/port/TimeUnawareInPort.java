/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.port;

import montiarc.rte.port.messages.IgnoreTicksMessageFilter;

/**
 * An incoming port of a MontiArc component that cannot receive ticks.
 *
 * @param <DataType> the type that can be received via this port
 */
public abstract class TimeUnawareInPort<DataType> extends AbstractInPort<DataType>
    implements IgnoreTicksMessageFilter<DataType> {
  
  public TimeUnawareInPort(String qualifiedName) {
    super(qualifiedName);
  }
}