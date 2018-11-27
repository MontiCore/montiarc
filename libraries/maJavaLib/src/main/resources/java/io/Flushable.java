/* (c) https://github.com/MontiCore/monticore */

package java.io;

import java.io.IOException;

/**
 * A <tt>Flushable</tt> is a destination of data that can be flushed.  The
 * flush method is invoked to write any buffered output to the underlying
 * stream.
 *
 * @version %I% %E%
 * @since 1.5
 */

public interface Flushable {

  /**
   * Flushes this stream by writing any buffered output to the underlying
   * stream.
   *
   * @throws IOException If an I/O error occurs
   */
  void flush() throws IOException;
}
