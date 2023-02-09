/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.port.messages;

/**
 * Implementation of a {@link MessageFilteringStrategy} which does not allow {@link Message#tick ticks}.
 *
 * @param <DataType> the type of message the filter applies to
 */
public interface IgnoreTicksMessageFilter<DataType> extends MessageFilteringStrategy<DataType> {
  
  @Override
  default boolean messageIsValidOnPort(Message<DataType> message) {
    return message != Message.tick;
  }
}
