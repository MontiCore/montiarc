//* (c) https://github.com/MontiCore/monticore */

package java.lang.instrument;

import java.lang.Exception;
/**
 * Thrown by an implementation of
 * {@link java.lang.instrument.Instrumentation#redefineClasses Instrumentation.redefineClasses}
 * when one of the specified classes cannot be modified.
 *
 * @see     java.lang.instrument.Instrumentation#redefineClasses
 * @since   1.5
 */
public class UnmodifiableClassException extends Exception {
  /**
   * Constructs an <code>UnmodifiableClassException</code> with no
   * detail message.
   */
  public
  UnmodifiableClassException() {
    super();
  }

  /**
   * Constructs an <code>UnmodifiableClassException</code> with the
   * specified detail message.
   *
   * @param   s   the detail message.
   */
  public
  UnmodifiableClassException(String s) {
    super(s);
  }
}
