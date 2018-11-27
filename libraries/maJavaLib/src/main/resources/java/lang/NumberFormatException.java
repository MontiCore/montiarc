/* (c) https://github.com/MontiCore/monticore */

package java.lang;

/**
 * Thrown to indicate that the application has attempted to convert
 * a string to one of the numeric types, but that the string does not
 * have the appropriate format.
 *
 * @author  unascribed
 * @version %I%, %G%
 * @see     java.lang.Integer#toString()
 * @since   JDK1.0
 */
public
class NumberFormatException extends IllegalArgumentException {
  static final long serialVersionUID = -2848938806368998894L;

  /**
   * Constructs a <code>NumberFormatException</code> with no detail message.
   */
  public NumberFormatException () {
    super();
  }

  /**
   * Constructs a <code>NumberFormatException</code> with the
   * specified detail message.
   *
   * @param   s   the detail message.
   */
  public NumberFormatException (String s) {
    super (s);
  }

  /**
   * Factory method for making a <code>NumberFormatException</code>
   * given the specified input which caused the error.
   *
   * @param   s   the input causing the error
   */
  static NumberFormatException forInputString(String s) {
    return new NumberFormatException("For input string: \"" + s + "\"");
  }
}
