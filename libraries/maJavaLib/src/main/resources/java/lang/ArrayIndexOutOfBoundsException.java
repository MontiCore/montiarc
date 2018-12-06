/* (c) https://github.com/MontiCore/monticore */

package java.lang;

/**
 * Thrown to indicate that an array has been accessed with an
 * illegal index. The index is either negative or greater than or
 * equal to the size of the array.
 *
 * @author  unascribed
 * @version %I%, %G%
 * @since   JDK1.0
 */
public
class ArrayIndexOutOfBoundsException extends IndexOutOfBoundsException {
  /**
   * Constructs an <code>ArrayIndexOutOfBoundsException</code> with no
   * detail message.
   */
  public ArrayIndexOutOfBoundsException() {
    super();
  }

  /**
   * Constructs a new <code>ArrayIndexOutOfBoundsException</code>
   * class with an argument indicating the illegal index.
   *
   * @param   index   the illegal index.
   */
  public ArrayIndexOutOfBoundsException(int index) {
    super("Array index out of range: " + index);
  }

  /**
   * Constructs an <code>ArrayIndexOutOfBoundsException</code> class
   * with the specified detail message.
   *
   * @param   s   the detail message.
   */
  public ArrayIndexOutOfBoundsException(String s) {
    super(s);
  }
}
