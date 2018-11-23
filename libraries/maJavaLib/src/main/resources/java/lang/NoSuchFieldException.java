/* (c) https://github.com/MontiCore/monticore */

package java.lang;

/**
 * Signals that the class doesn't have a field of a specified name.
 *
 * @author  unascribed
 * @version %I%, %G%
 * @since   JDK1.1
 */
public class NoSuchFieldException extends Exception {
  /**
   * Constructor.
   */
  public NoSuchFieldException() {
    super();
  }

  /**
   * Constructor with a detail message.
   *
   * @param s the detail message
   */
  public NoSuchFieldException(String s) {
    super(s);
  }
}
