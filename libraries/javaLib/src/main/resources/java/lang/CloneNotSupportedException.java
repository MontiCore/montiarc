/* (c) https://github.com/MontiCore/monticore */

package java.lang;

/**
 * Thrown to indicate that the <code>clone</code> method in class
 * <code>Object</code> has been called to clone an object, but that
 * the object's class does not implement the <code>Cloneable</code>
 * interface.
 * <p>
 * Applications that override the <code>clone</code> method can also
 * throw this exception to indicate that an object could not or
 * should not be cloned.
 *
 * @author  unascribed
 * @version %I%, %G%
 * @see     java.lang.Cloneable
 * @see     java.lang.Object#clone()
 * @since   JDK1.0
 */

public
class CloneNotSupportedException extends Exception {
  /**
   * Constructs a <code>CloneNotSupportedException</code> with no
   * detail message.
   */
  public CloneNotSupportedException() {
    super();
  }

  /**
   * Constructs a <code>CloneNotSupportedException</code> with the
   * specified detail message.
   *
   * @param   s   the detail message.
   */
  public CloneNotSupportedException(String s) {
    super(s);
  }
}
