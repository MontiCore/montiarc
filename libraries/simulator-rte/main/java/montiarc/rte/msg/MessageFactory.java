/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.msg;

public final class MessageFactory {
  
  private MessageFactory() { }

  /**
   * Short form for {@code Message.of(value)}
   * @see Message#of(Object)
   */
  public static <T> Message<T> msg(T value) {
    return Message.of(value);
  }

  /**
   * Short form for {@code Tick.get()}
   * @see Tick#get()
   */
  public static <T> Message<T> tk() {
    return Tick.get();
  }
}
