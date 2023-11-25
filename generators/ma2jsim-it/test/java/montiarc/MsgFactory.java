/* (c) https://github.com/MontiCore/monticore */
package montiarc;

import montiarc.rte.msg.Message;
import montiarc.rte.msg.Tick;

public final class MsgFactory {
  private MsgFactory() { }

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
