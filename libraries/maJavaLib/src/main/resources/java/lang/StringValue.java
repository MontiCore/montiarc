/* (c) https://github.com/MontiCore/monticore */

package java.lang;

import java.util.Arrays;

/**
 * This class consists exclusively of static methods that operate on
 * character arrays used by Strings for storing the value.
 */

class StringValue {
  private StringValue() { }

  /**
   * Returns a char array that is a copy of the given char array.
   */
  static char[] from(char[] value) {
    return Arrays.copyOf(value, value.length);
  }
}
