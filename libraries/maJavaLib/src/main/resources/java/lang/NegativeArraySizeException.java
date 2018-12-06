/* (c) https://github.com/MontiCore/monticore */

package java.lang;

/**
 * Thrown if an application tries to create an array with negative size.
 *
 * @author  unascribed
 * @version %I%, %G%
 * @since   JDK1.0
 */
public class NegativeArraySizeException extends RuntimeException {
  /**
   * Constructs a <code>NegativeArraySizeException</code> with no
   * detail message.
   */
  public NegativeArraySizeException() {
    super();
  }

  /**
   * Constructs a <code>NegativeArraySizeException</code> with the
   * specified detail message.
   *
   * @param   s   the detail message.
   */
  public NegativeArraySizeException(String s) {
    super(s);
  }
}
