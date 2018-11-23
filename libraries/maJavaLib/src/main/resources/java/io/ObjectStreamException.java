/* (c) https://github.com/MontiCore/monticore */

package java.io;

/**
 * Superclass of all exceptions specific to Object Stream classes.
 *
 * @author  unascribed
 * @version %I%, %G%
 * @since   JDK1.1
 */
public abstract class ObjectStreamException extends IOException {

  private static final long serialVersionUID = 7260898174833392607L;

  /**
   * Create an ObjectStreamException with the specified argument.
   *
   * @param classname the detailed message for the exception
   */
  protected ObjectStreamException(String classname) {
    super(classname);
  }

  /**
   * Create an ObjectStreamException.
   */
  protected ObjectStreamException() {
    super();
  }
}
