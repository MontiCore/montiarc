/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.msg;

/**
 * A filter that prevents all ticks {@link Tick#get()}.
 *
 * @param <T> the message's data type the filter applies to
 */
public interface IgnoreTicksMessageFilter<T> extends MessageFilteringStrategy<T> {

  @Override
  default boolean messageIsValidOnPort(Message<? extends T> message) {
    return message != Tick.get();
  }
}
