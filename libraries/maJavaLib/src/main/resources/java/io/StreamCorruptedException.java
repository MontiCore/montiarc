/* (c) https://github.com/MontiCore/monticore */

package java.io;

/**
 * Thrown when control information that was read from an object stream
 * violates internal consistency checks.
 *
 * @author  unascribed
 * @version %I%, %G%
 * @since   JDK1.1
 */
public class StreamCorruptedException extends ObjectStreamException {

  private static final long serialVersionUID = 8983558202217591746L;

  /**
   * Create a StreamCorruptedException and list a reason why thrown.
   *
   * @param reason  String describing the reason for the exception.
   */
  public StreamCorruptedException(String reason) {
    super(reason);
  }

  /**
   * Create a StreamCorruptedException and list no reason why thrown.
   */
  public StreamCorruptedException() {
    super();
  }
}
