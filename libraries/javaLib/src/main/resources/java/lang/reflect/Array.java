/* (c) https://github.com/MontiCore/monticore */

package java.lang.reflect;

/**
 * The <code>Array</code> class provides static methods to dynamically create and
 * access Java arrays.
 *
 * <p><code>Array</code> permits widening conversions to occur during a get or set
 * operation, but throws an <code>IllegalArgumentException</code> if a narrowing
 * conversion would occur.
 *
 * @author Nakul Saraiya
 */
import java.lang.Object;
import java.lang.NegativeArraySizeException;
import java.lang.IllegalArgumentException;
import java.lang.ArrayIndexOutOfBoundsException;
import java.lang.Class;

public final
class Array {

  /**
   * Constructor.  Class Array is not instantiable.
   */
  private Array() {}

  /**
   * Creates a new array with the specified component type and
   * length.
   * Invoking this method is equivalent to creating an array
   * as follows:
   * <blockquote>
   * <pre>
   * int[] x = {length};
   * Array.newInstance(componentType, x);
   * </pre>
   * </blockquote>
   *
   * @param componentType the <code>Class</code> object representing the
   * component type of the new array
   * @param length the length of the new array
   * @return the new array
   * @exception NullPointerException if the specified
   * <code>componentType</code> parameter is null
   * @exception IllegalArgumentException if componentType is {@link Void#TYPE}
   * @exception NegativeArraySizeException if the specified <code>length</code>
   * is negative
   */
  public static Object newInstance(Class<?> componentType, int length)
      throws NegativeArraySizeException {
    return newArray(componentType, length);
  }

  /**
   * Creates a new array
   * with the specified component type and dimensions.
   * If <code>componentType</code>
   * represents a non-array class or interface, the new array
   * has <code>dimensions.length</code> dimensions and
   * <code>componentType</code> as its component type. If
   * <code>componentType</code> represents an array class, the
   * number of dimensions of the new array is equal to the sum
   * of <code>dimensions.length</code> and the number of
   * dimensions of <code>componentType</code>. In this case, the
   * component type of the new array is the component type of
   * <code>componentType</code>.
   *
   * <p>The number of dimensions of the new array must not
   * exceed the number of array dimensions supported by the
   * implementation (typically 255).
   *
   * @param componentType the <code>Class</code> object representing the component
   * type of the new array
   * @param dimensions an array of <code>int</code> representing the dimensions of
   * the new array
   * @return the new array
   * @exception NullPointerException if the specified
   * <code>componentType</code> argument is null
   * @exception IllegalArgumentException if the specified <code>dimensions</code>
   * argument is a zero-dimensional array, or if the number of
   * requested dimensions exceeds the limit on the number of array dimensions
   * supported by the implementation (typically 255), or if componentType
   * is {@link Void#TYPE}.
   * @exception NegativeArraySizeException if any of the components in
   * the specified <code>dimensions</code> argument is negative.
   */
//  public static Object newInstance(Class<?> componentType, int... dimensions)
//      throws IllegalArgumentException, NegativeArraySizeException {
//    return multiNewArray(componentType, dimensions);
//  }

  /*
  Reason for making such change was because last formal parameter int... is not created as an array[].
   */
    public static Object newInstance(Class<?> componentType, int[] dimensions)
        throws IllegalArgumentException, NegativeArraySizeException {
      return multiNewArray(componentType, dimensions);
    }

  /**
   * Returns the length of the specified array object, as an <code>int</code>.
   *
   * @param array the array
   * @return the length of the array
   * @exception IllegalArgumentException if the object argument is not
   * an array
   */
  public static native int getLength(Object array)
      throws IllegalArgumentException;

  /**
   * Returns the value of the indexed component in the specified
   * array object.  The value is automatically wrapped in an object
   * if it has a primitive type.
   *
   * @param array the array
   * @param index the index
   * @return the (possibly wrapped) value of the indexed component in
   * the specified array
   * @exception NullPointerException If the specified object is null
   * @exception IllegalArgumentException If the specified object is not
   * an array
   * @exception ArrayIndexOutOfBoundsException If the specified <code>index</code>
   * argument is negative, or if it is greater than or equal to the
   * length of the specified array
   */
  public static native Object get(Object array, int index)
      throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

  /**
   * Returns the value of the indexed component in the specified
   * array object, as a <code>boolean</code>.
   *
   * @param array the array
   * @param index the index
   * @return the value of the indexed component in the specified array
   * @exception NullPointerException If the specified object is null
   * @exception IllegalArgumentException If the specified object is not
   * an array, or if the indexed element cannot be converted to the
   * return type by an identity or widening conversion
   * @exception ArrayIndexOutOfBoundsException If the specified <code>index</code>
   * argument is negative, or if it is greater than or equal to the
   * length of the specified array
   * @see Array#get
   */
  public static native boolean getBoolean(Object array, int index)
      throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

  /**
   * Returns the value of the indexed component in the specified
   * array object, as a <code>byte</code>.
   *
   * @param array the array
   * @param index the index
   * @return the value of the indexed component in the specified array
   * @exception NullPointerException If the specified object is null
   * @exception IllegalArgumentException If the specified object is not
   * an array, or if the indexed element cannot be converted to the
   * return type by an identity or widening conversion
   * @exception ArrayIndexOutOfBoundsException If the specified <code>index</code>
   * argument is negative, or if it is greater than or equal to the
   * length of the specified array
   * @see Array#get
   */
  public static native byte getByte(Object array, int index)
      throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

  /**
   * Returns the value of the indexed component in the specified
   * array object, as a <code>char</code>.
   *
   * @param array the array
   * @param index the index
   * @return the value of the indexed component in the specified array
   * @exception NullPointerException If the specified object is null
   * @exception IllegalArgumentException If the specified object is not
   * an array, or if the indexed element cannot be converted to the
   * return type by an identity or widening conversion
   * @exception ArrayIndexOutOfBoundsException If the specified <code>index</code>
   * argument is negative, or if it is greater than or equal to the
   * length of the specified array
   * @see Array#get
   */
  public static native char getChar(Object array, int index)
      throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

  /**
   * Returns the value of the indexed component in the specified
   * array object, as a <code>short</code>.
   *
   * @param array the array
   * @param index the index
   * @return the value of the indexed component in the specified array
   * @exception NullPointerException If the specified object is null
   * @exception IllegalArgumentException If the specified object is not
   * an array, or if the indexed element cannot be converted to the
   * return type by an identity or widening conversion
   * @exception ArrayIndexOutOfBoundsException If the specified <code>index</code>
   * argument is negative, or if it is greater than or equal to the
   * length of the specified array
   * @see Array#get
   */
  public static native short getShort(Object array, int index)
      throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

  /**
   * Returns the value of the indexed component in the specified
   * array object, as an <code>int</code>.
   *
   * @param array the array
   * @param index the index
   * @return the value of the indexed component in the specified array
   * @exception NullPointerException If the specified object is null
   * @exception IllegalArgumentException If the specified object is not
   * an array, or if the indexed element cannot be converted to the
   * return type by an identity or widening conversion
   * @exception ArrayIndexOutOfBoundsException If the specified <code>index</code>
   * argument is negative, or if it is greater than or equal to the
   * length of the specified array
   * @see Array#get
   */
  public static native int getInt(Object array, int index)
      throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

  /**
   * Returns the value of the indexed component in the specified
   * array object, as a <code>long</code>.
   *
   * @param array the array
   * @param index the index
   * @return the value of the indexed component in the specified array
   * @exception NullPointerException If the specified object is null
   * @exception IllegalArgumentException If the specified object is not
   * an array, or if the indexed element cannot be converted to the
   * return type by an identity or widening conversion
   * @exception ArrayIndexOutOfBoundsException If the specified <code>index</code>
   * argument is negative, or if it is greater than or equal to the
   * length of the specified array
   * @see Array#get
   */
  public static native long getLong(Object array, int index)
      throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

  /**
   * Returns the value of the indexed component in the specified
   * array object, as a <code>float</code>.
   *
   * @param array the array
   * @param index the index
   * @return the value of the indexed component in the specified array
   * @exception NullPointerException If the specified object is null
   * @exception IllegalArgumentException If the specified object is not
   * an array, or if the indexed element cannot be converted to the
   * return type by an identity or widening conversion
   * @exception ArrayIndexOutOfBoundsException If the specified <code>index</code>
   * argument is negative, or if it is greater than or equal to the
   * length of the specified array
   * @see Array#get
   */
  public static native float getFloat(Object array, int index)
      throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

  /**
   * Returns the value of the indexed component in the specified
   * array object, as a <code>double</code>.
   *
   * @param array the array
   * @param index the index
   * @return the value of the indexed component in the specified array
   * @exception NullPointerException If the specified object is null
   * @exception IllegalArgumentException If the specified object is not
   * an array, or if the indexed element cannot be converted to the
   * return type by an identity or widening conversion
   * @exception ArrayIndexOutOfBoundsException If the specified <code>index</code>
   * argument is negative, or if it is greater than or equal to the
   * length of the specified array
   * @see Array#get
   */
  public static native double getDouble(Object array, int index)
      throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

  /**
   * Sets the value of the indexed component of the specified array
   * object to the specified new value.  The new value is first
   * automatically unwrapped if the array has a primitive component
   * type.
   * @param array the array
   * @param index the index into the array
   * @param value the new value of the indexed component
   * @exception NullPointerException If the specified object argument
   * is null
   * @exception IllegalArgumentException If the specified object argument
   * is not an array, or if the array component type is primitive and
   * an unwrapping conversion fails
   * @exception ArrayIndexOutOfBoundsException If the specified <code>index</code>
   * argument is negative, or if it is greater than or equal to
   * the length of the specified array
   */
  public static native void set(Object array, int index, Object value)
      throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

  /**
   * Sets the value of the indexed component of the specified array
   * object to the specified <code>boolean</code> value.
   * @param array the array
   * @param index the index into the array
   * @param z the new value of the indexed component
   * @exception NullPointerException If the specified object argument
   * is null
   * @exception IllegalArgumentException If the specified object argument
   * is not an array, or if the specified value cannot be converted
   * to the underlying array's component type by an identity or a
   * primitive widening conversion
   * @exception ArrayIndexOutOfBoundsException If the specified <code>index</code>
   * argument is negative, or if it is greater than or equal to
   * the length of the specified array
   * @see Array#set
   */
  public static native void setBoolean(Object array, int index, boolean z)
      throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

  /**
   * Sets the value of the indexed component of the specified array
   * object to the specified <code>byte</code> value.
   * @param array the array
   * @param index the index into the array
   * @param b the new value of the indexed component
   * @exception NullPointerException If the specified object argument
   * is null
   * @exception IllegalArgumentException If the specified object argument
   * is not an array, or if the specified value cannot be converted
   * to the underlying array's component type by an identity or a
   * primitive widening conversion
   * @exception ArrayIndexOutOfBoundsException If the specified <code>index</code>
   * argument is negative, or if it is greater than or equal to
   * the length of the specified array
   * @see Array#set
   */
  public static native void setByte(Object array, int index, byte b)
      throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

  /**
   * Sets the value of the indexed component of the specified array
   * object to the specified <code>char</code> value.
   * @param array the array
   * @param index the index into the array
   * @param c the new value of the indexed component
   * @exception NullPointerException If the specified object argument
   * is null
   * @exception IllegalArgumentException If the specified object argument
   * is not an array, or if the specified value cannot be converted
   * to the underlying array's component type by an identity or a
   * primitive widening conversion
   * @exception ArrayIndexOutOfBoundsException If the specified <code>index</code>
   * argument is negative, or if it is greater than or equal to
   * the length of the specified array
   * @see Array#set
   */
  public static native void setChar(Object array, int index, char c)
      throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

  /**
   * Sets the value of the indexed component of the specified array
   * object to the specified <code>short</code> value.
   * @param array the array
   * @param index the index into the array
   * @param s the new value of the indexed component
   * @exception NullPointerException If the specified object argument
   * is null
   * @exception IllegalArgumentException If the specified object argument
   * is not an array, or if the specified value cannot be converted
   * to the underlying array's component type by an identity or a
   * primitive widening conversion
   * @exception ArrayIndexOutOfBoundsException If the specified <code>index</code>
   * argument is negative, or if it is greater than or equal to
   * the length of the specified array
   * @see Array#set
   */
  public static native void setShort(Object array, int index, short s)
      throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

  /**
   * Sets the value of the indexed component of the specified array
   * object to the specified <code>int</code> value.
   * @param array the array
   * @param index the index into the array
   * @param i the new value of the indexed component
   * @exception NullPointerException If the specified object argument
   * is null
   * @exception IllegalArgumentException If the specified object argument
   * is not an array, or if the specified value cannot be converted
   * to the underlying array's component type by an identity or a
   * primitive widening conversion
   * @exception ArrayIndexOutOfBoundsException If the specified <code>index</code>
   * argument is negative, or if it is greater than or equal to
   * the length of the specified array
   * @see Array#set
   */
  public static native void setInt(Object array, int index, int i)
      throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

  /**
   * Sets the value of the indexed component of the specified array
   * object to the specified <code>long</code> value.
   * @param array the array
   * @param index the index into the array
   * @param l the new value of the indexed component
   * @exception NullPointerException If the specified object argument
   * is null
   * @exception IllegalArgumentException If the specified object argument
   * is not an array, or if the specified value cannot be converted
   * to the underlying array's component type by an identity or a
   * primitive widening conversion
   * @exception ArrayIndexOutOfBoundsException If the specified <code>index</code>
   * argument is negative, or if it is greater than or equal to
   * the length of the specified array
   * @see Array#set
   */
  public static native void setLong(Object array, int index, long l)
      throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

  /**
   * Sets the value of the indexed component of the specified array
   * object to the specified <code>float</code> value.
   * @param array the array
   * @param index the index into the array
   * @param f the new value of the indexed component
   * @exception NullPointerException If the specified object argument
   * is null
   * @exception IllegalArgumentException If the specified object argument
   * is not an array, or if the specified value cannot be converted
   * to the underlying array's component type by an identity or a
   * primitive widening conversion
   * @exception ArrayIndexOutOfBoundsException If the specified <code>index</code>
   * argument is negative, or if it is greater than or equal to
   * the length of the specified array
   * @see Array#set
   */
  public static native void setFloat(Object array, int index, float f)
      throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

  /**
   * Sets the value of the indexed component of the specified array
   * object to the specified <code>double</code> value.
   * @param array the array
   * @param index the index into the array
   * @param d the new value of the indexed component
   * @exception NullPointerException If the specified object argument
   * is null
   * @exception IllegalArgumentException If the specified object argument
   * is not an array, or if the specified value cannot be converted
   * to the underlying array's component type by an identity or a
   * primitive widening conversion
   * @exception ArrayIndexOutOfBoundsException If the specified <code>index</code>
   * argument is negative, or if it is greater than or equal to
   * the length of the specified array
   * @see Array#set
   */
  public static native void setDouble(Object array, int index, double d)
      throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

   /*
  Private
  */

  private static native Object newArray(Class componentType, int length)
      throws NegativeArraySizeException;


  private static native Object multiNewArray(Class componentType,
      int[] dimensions)
      throws IllegalArgumentException, NegativeArraySizeException;


}
