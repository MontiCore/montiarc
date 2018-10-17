/* (c) https://github.com/MontiCore/monticore */

package java.lang.reflect;

/**
 * WildcardType represents a wildcard type expression, such as
 * <tt>?</tt>, <tt>? extends Number</tt>, or <tt>? super Integer</tt>.
 *
 * @since 1.5
 */
public interface WildcardType extends Type {
  /**
   * Returns an array of <tt>Type</tt> objects representing the  upper
   * bound(s) of this type variable.  Note that if no upper bound is
   * explicitly declared, the upper bound is <tt>Object</tt>.
   *
   * <p>For each upper bound B :
   * <ul>
   *  <li>if B is a parameterized type or a type variable, it is created,
   *  (see {@link java.lang.reflect.ParameterizedType ParameterizedType}
   *  for the details of the creation process for parameterized types).
   *  <li>Otherwise, B is resolved.
   * </ul>
   *
   * @return an array of Types representing the upper bound(s) of this
   *     type variable
   * @throws TypeNotPresentException if any of the
   *     bounds refers to a non-existent type declaration
   * @throws MalformedParameterizedTypeException if any of the
   *     bounds refer to a parameterized type that cannot be instantiated
   *     for any reason
   */
  Type[] getUpperBounds();

  /**
   * Returns an array of <tt>Type</tt> objects representing the
   * lower bound(s) of this type variable.  Note that if no lower bound is
   * explicitly declared, the lower bound is the type of <tt>null</tt>.
   * In this case, a zero length array is returned.
   *
   * <p>For each lower bound B :
   * <ul>
   *   <li>if B is a parameterized type or a type variable, it is created,
   *  (see {@link java.lang.reflect.ParameterizedType ParameterizedType}
   *  for the details of the creation process for parameterized types).
   *   <li>Otherwise, B is resolved.
   * </ul>
   *
   * @return an array of Types representing the lower bound(s) of this
   *     type variable
   * @throws TypeNotPresentException if any of the
   *     bounds refers to a non-existent type declaration
   * @throws MalformedParameterizedTypeException if any of the
   *     bounds refer to a parameterized type that cannot be instantiated
   *     for any reason
   */
  Type[] getLowerBounds();
  // one or many? Up to language spec; currently only one, but this API
  // allows for generalization.
}

