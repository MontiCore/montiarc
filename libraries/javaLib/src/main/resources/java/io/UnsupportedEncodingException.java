/* (c) https://github.com/MontiCore/monticore */
package java.io;

/**
 * The Character Encoding is not supported.
 *
 * @author  Asmus Freytag
 * @version %I%, %G%
 * @since   JDK1.1
 */
public class UnsupportedEncodingException
    extends IOException
{
  /**
   * Constructs an UnsupportedEncodingException without a detail message.
   */
  public UnsupportedEncodingException() {
    super();
  }

  /**
   * Constructs an UnsupportedEncodingException with a detail message.
   * @param s Describes the reason for the exception.
   */
  public UnsupportedEncodingException(String s) {
    super(s);
  }
}
