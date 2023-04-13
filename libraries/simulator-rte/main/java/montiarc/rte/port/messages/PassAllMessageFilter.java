/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.port.messages;

/**
 * Implementation of a {@link MessageFilteringStrategy} which permits all messages.
 *
 * @param <T> the type of message the filter applies to
 */
public interface PassAllMessageFilter<T> extends MessageFilteringStrategy<T> {

  @Override
  default boolean messageIsValidOnPort(Message<T> message) {
    return true;
  }
}
