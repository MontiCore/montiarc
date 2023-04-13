/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.port.messages;

/**
 * Implementation of a {@link MessageFilteringStrategy} which does not allow {@link Message#tick ticks}.
 *
 * @param <T> the type of message the filter applies to
 */
public interface IgnoreTicksMessageFilter<T> extends MessageFilteringStrategy<T> {

  @Override
  default boolean messageIsValidOnPort(Message<T> message) {
    return message != Message.tick;
  }
}
