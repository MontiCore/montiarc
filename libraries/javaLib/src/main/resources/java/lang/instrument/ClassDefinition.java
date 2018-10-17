//* (c) https://github.com/MontiCore/monticore */

package java.lang.instrument;

import java.lang.Class;
import java.lang.NullPointerException;

/**
 * This class serves as a parameter block to the <code>Instrumentation.redefineClasses</code> method.
 * Serves to bind the <code>Class</code> that needs redefining together with the new class file bytes.
 *
 * @see     java.lang.instrument.Instrumentation#redefineClasses
 * @since   1.5
 */
public final class ClassDefinition {
  /**
   *  The class to redefine
   */
  private final   Class   mClass;

  /**
   *  The replacement class file bytes
   */
  private final   byte[]  mClassFile;

  /**
   *  Creates a new <code>ClassDefinition</code> binding using the supplied
   *  class and class file bytes. Does not copy the supplied buffer, just captures a reference to it.
   *
   * @param theClass the <code>Class</code> that needs redefining
   * @param theClassFile the new class file bytes
   *
   * @throws java.lang.NullPointerException if the supplied class or array is <code>null</code>.
   */
  public
  ClassDefinition(    Class<?> theClass,
      byte[]  theClassFile) {
    if (theClass == null || theClassFile == null) {
      throw new NullPointerException();
    }
    mClass      = theClass;
    mClassFile  = theClassFile;
  }

  /**
   * Returns the class.
   *
   * @return    the <code>Class</code> object referred to.
   */
  public Class<?>
  getDefinitionClass() {
    return mClass;
  }

  /**
   * Returns the array of bytes that contains the new class file.
   *
   * @return    the class file bytes.
   */
  public byte[]
  getDefinitionClassFile() {
    return mClassFile;
  }
}
