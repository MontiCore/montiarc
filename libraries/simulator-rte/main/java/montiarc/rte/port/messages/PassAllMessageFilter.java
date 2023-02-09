/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.port.messages;

/**
 * Implementation of a {@link MessageFilteringStrategy} which permits all messages.
 *
 * @param <DataType> the type of message the filter applies to
 */
public interface PassAllMessageFilter<DataType> extends MessageFilteringStrategy<DataType> {
  
  @Override
  default boolean messageIsValidOnPort(Message<DataType> message) {
    return true;
  }
}
