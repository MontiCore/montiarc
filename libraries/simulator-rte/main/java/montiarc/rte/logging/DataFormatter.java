/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.logging;

import montiarc.rte.msg.Message;
import montiarc.rte.msg.Tick;

/** Formats MontiArc data objects to a human-readable String */
public class DataFormatter {
  private DataFormatter() {}

  /**
   * Formats
   * <list>
   *   <li>Wrapped primitives to their string representation</li>
   *   <li>Strings to be enclosed in ""</li>
   *   <li>{@link Message} objects to the string representation of the data inside</li>
   *   <li>{@link Tick} to "√"</li>
   *   <li>Enum values to their constant names</li>
   *   <li>Other objects to their JSON representation</li>
   * </list>
   */
  public static String format(Object data) {
    if (data == null) {
      return "null";
    } else if (data instanceof Boolean || data instanceof Number || data instanceof Enum) {
      return data.toString();
    } else if (data instanceof Character){
      return format(((Character) data).charValue());
    } else if (data instanceof String) {
      return "\"" + data + "\"";
    } else if (data instanceof Tick) {
      return "√";
    } else if (data instanceof Message) {
      return format(((Message<?>) data).getData());
    } else {
      return "Object of " + Object.class.getCanonicalName();
    }
  }

  public static String format(char data) {
    return "'" + data + "'";
  }

  public static String format(boolean data) {
    return "" + data;
  }

  public static String format(byte data) {
    return "" + data;
  }

  public static String format(short data) {
    return "" + data;
  }

  public static String format(int data) {
    return "" + data;
  }

  public static String format(long data) {
    return "" + data;
  }

  public static String format(float data) {
    return "" + data;
  }

  public static String format(double data) {
    return "" + data;
  }
}
