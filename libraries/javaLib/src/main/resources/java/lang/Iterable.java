/* (c) https://github.com/MontiCore/monticore */

package java.lang;

import java.util.Iterator;

/** Implementing this interface allows an object to be the target of
 *  the "foreach" statement.
 * @since 1.5
 */
public interface Iterable<T> {

  /**
   * Returns an iterator over a set of elements of type T.
   *
   * @return an Iterator.
   */
  Iterator<T> iterator();
}
