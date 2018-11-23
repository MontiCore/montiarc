/* (c) https://github.com/MontiCore/monticore */

package java.lang.instrument;

import java.lang.Exception;
/*
 * Copyright 2003 Wily Technology, Inc.
 */

/**
 * Thrown by an implementation of
 * {@link java.lang.instrument.ClassFileTransformer#transform ClassFileTransformer.transform}
 * when its input parameters are invalid.
 * This may occur either because the initial class file bytes were
 * invalid or a previously applied transform corrupted the bytes.
 *
 * @see     java.lang.instrument.ClassFileTransformer#transform
 * @since   1.5
 */
public class IllegalClassFormatException extends Exception {
  /**
   * Constructs an <code>IllegalClassFormatException</code> with no
   * detail message.
   */
  public
  IllegalClassFormatException() {
    super();
  }

  /**
   * Constructs an <code>IllegalClassFormatException</code> with the
   * specified detail message.
   *
   * @param   s   the detail message.
   */
  public
  IllegalClassFormatException(String s) {
    super(s);
  }
}
