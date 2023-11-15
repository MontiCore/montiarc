/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.msg;

/**
 * Implementation of a {@link MessageFilteringStrategy} which permits all messages.
 *
 * @param <T> the message's data type the filter applies to
 */
public interface PassAllMessageFilter<T> extends MessageFilteringStrategy<T> {

  @Override
  default boolean messageIsValidOnPort(Message<? extends T> message) {
    return true;
  }
}
