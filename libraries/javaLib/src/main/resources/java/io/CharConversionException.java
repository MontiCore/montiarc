/* (c) https://github.com/MontiCore/monticore */
package java.io;

/**
 * Base class for character conversion exceptions.
 *
 * @author      Asmus Freytag
 * @version 	%I%, %G%
 * @since       JDK1.1
 */
import java.lang.String;

public class CharConversionException
    extends IOException
{
  /**
   * This provides no detailed message.
   */
  public CharConversionException() {
  }
  /**
   * This provides a detailed message.
   *
   * @param s the detailed message associated with the exception.
   */
  public CharConversionException(String s) {
    super(s);
  }
}