/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.port.messages;

/**
 * This interface defines a method used to filter messages sent via ports.
 *
 * @param <DataType> the type of message the filter applies to
 */
public interface MessageFilteringStrategy<DataType> {
  
  /**
   * This method determines whether the given message is valid on the given port.
   * If it is valid, the message is received (incoming) or sent (outgoing).
   * <br>
   * This should be used in untimed ports to filter out ticks
   * so that information about the passing of time is only available in appropriate places.
   *
   * @param message the message under consideration
   *
   * @return true iff this port should pass the given message
   */
  boolean messageIsValidOnPort(Message<DataType> message);
}
