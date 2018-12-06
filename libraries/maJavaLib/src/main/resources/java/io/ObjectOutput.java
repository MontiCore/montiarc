/* (c) https://github.com/MontiCore/monticore */

package java.io;

/**
 * ObjectOutput extends the DataOutput interface to include writing of objects.
 * DataOutput includes methods for output of primitive types, ObjectOutput
 * extends that interface to include objects, arrays, and Strings.
 *
 * @author  unascribed
 * @version %I%, %G%
 * @see java.io.InputStream
 * @see java.io.ObjectOutputStream
 * @see java.io.ObjectInputStream
 * @since   JDK1.1
 */
public interface ObjectOutput extends DataOutput {
  /**
   * Write an object to the underlying storage or stream.  The
   * class that implements this interface defines how the object is
   * written.
   *
   * @param obj the object to be written
   * @exception IOException Any of the usual Input/Output related exceptions.
   */
  public void writeObject(Object obj)
      throws IOException;

  /**
   * Writes a byte. This method will block until the byte is actually
   * written.
   * @param b	the byte
   * @exception IOException If an I/O error has occurred.
   */
  public void write(int b) throws IOException;

  /**
   * Writes an array of bytes. This method will block until the bytes
   * are actually written.
   * @param b	the data to be written
   * @exception IOException If an I/O error has occurred.
   */
  public void write(byte b[]) throws IOException;

  /**
   * Writes a sub array of bytes.
   * @param b	the data to be written
   * @param off	the start offset in the data
   * @param len	the number of bytes that are written
   * @exception IOException If an I/O error has occurred.
   */
  public void write(byte b[], int off, int len) throws IOException;

  /**
   * Flushes the stream. This will write any buffered
   * output bytes.
   * @exception IOException If an I/O error has occurred.
   */
  public void flush() throws IOException;

  /**
   * Closes the stream. This method must be called
   * to release any resources associated with the
   * stream.
   * @exception IOException If an I/O error has occurred.
   */
  public void close() throws IOException;
}
