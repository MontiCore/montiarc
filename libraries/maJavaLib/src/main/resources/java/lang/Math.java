/* (c) https://github.com/MontiCore/monticore */

package java.lang;


import java.util.Random;


/**
 * The class <code>Math</code> contains methods for performing basic
 * numeric operations such as the elementary exponential, logarithm,
 * square root, and trigonometric functions.
 *
 * <p>Unlike some of the numeric methods of class
 * <code>StrictMath</code>, all implementations of the equivalent
 * functions of class <code>Math</code> are not defined to return the
 * bit-for-bit same results.  This relaxation permits
 * better-performing implementations where strict reproducibility is
 * not required.
 *
 * <p>By default many of the <code>Math</code> methods simply call
 * the equivalent method in <code>StrictMath</code> for their
 * implementation.  Code generators are encouraged to use
 * platform-specific native libraries or microprocessor instructions,
 * where available, to provide higher-performance implementations of
 * <code>Math</code> methods.  Such higher-performance
 * implementations still must conform to the specification for
 * <code>Math</code>.
 *
 * <p>The quality of implementation specifications concern two
 * properties, accuracy of the returned result and monotonicity of the
 * method.  Accuracy of the floating-point <code>Math</code> methods
 * is measured in terms of <i>ulps</i>, units in the last place.  For
 * a given floating-point format, an ulp of a specific real number
 * value is the distance between the two floating-point values
 * bracketing that numerical value.  When discussing the accuracy of a
 * method as a whole rather than at a specific argument, the number of
 * ulps cited is for the worst-case error at any argument.  If a
 * method always has an error less than 0.5 ulps, the method always
 * returns the floating-point number nearest the exact result; such a
 * method is <i>correctly rounded</i>.  A correctly rounded method is
 * generally the best a floating-point approximation can be; however,
 * it is impractical for many floating-point methods to be correctly
 * rounded.  Instead, for the <code>Math</code> class, a larger error
 * bound of 1 or 2 ulps is allowed for certain methods.  Informally,
 * with a 1 ulp error bound, when the exact result is a representable
 * number, the exact result should be returned as the computed result;
 * otherwise, either of the two floating-point values which bracket
 * the exact result may be returned.  For exact results large in
 * magnitude, one of the endpoints of the bracket may be infinite.
 * Besides accuracy at individual arguments, maintaining proper
 * relations between the method at different arguments is also
 * important.  Therefore, most methods with more than 0.5 ulp errors
 * are required to be <i>semi-monotonic</i>: whenever the mathematical
 * function is non-decreasing, so is the floating-point approximation,
 * likewise, whenever the mathematical function is non-increasing, so
 * is the floating-point approximation.  Not all approximations that
 * have 1 ulp accuracy will automatically meet the monotonicity
 * requirements.
 *
 * @author  unascribed
 * @author  Joseph D. Darcy
 * @version %I%, %G%
 * @since   JDK1.0
 */

public final class Math {

  /**
   * Don't let anyone instantiate this class.
   */
  private Math() {}

  /**
   * The <code>double</code> value that is closer than any other to
   * <i>e</i>, the base of the natural logarithms.
   */
  public static final double E = 2.7182818284590452354;

  /**
   * The <code>double</code> value that is closer than any other to
   * <i>pi</i>, the ratio of the circumference of a circle to its
   * diameter.
   */
  public static final double PI = 3.14159265358979323846;

  /**
   * Returns the trigonometric sine of an angle.  Special cases:
   * <ul><li>If the argument is NaN or an infinity, then the
   * result is NaN.
   * <li>If the argument is zero, then the result is a zero with the
   * same sign as the argument.</ul>
   *
   * <p>The computed result must be within 1 ulp of the exact result.
   * Results must be semi-monotonic.
   *
   * @param   a   an angle, in radians.
   * @return  the sine of the argument.
   */
  public static double sin(double a) {
    return StrictMath.sin(a); // default impl. delegates to StrictMath
  }

  /**
   * Returns the trigonometric cosine of an angle. Special cases:
   * <ul><li>If the argument is NaN or an infinity, then the
   * result is NaN.</ul>
   *
   * <p>The computed result must be within 1 ulp of the exact result.
   * Results must be semi-monotonic.
   *
   * @param   a   an angle, in radians.
   * @return  the cosine of the argument.
   */
  public static double cos(double a) {
    return StrictMath.cos(a); // default impl. delegates to StrictMath
  }

  /**
   * Returns the trigonometric tangent of an angle.  Special cases:
   * <ul><li>If the argument is NaN or an infinity, then the result
   * is NaN.
   * <li>If the argument is zero, then the result is a zero with the
   * same sign as the argument.</ul>
   *
   * <p>The computed result must be within 1 ulp of the exact result.
   * Results must be semi-monotonic.
   *
   * @param   a   an angle, in radians.
   * @return  the tangent of the argument.
   */
  public static double tan(double a) {
    return StrictMath.tan(a); // default impl. delegates to StrictMath
  }

  /**
   * Returns the arc sine of a value; the returned angle is in the
   * range -<i>pi</i>/2 through <i>pi</i>/2.  Special cases:
   * <ul><li>If the argument is NaN or its absolute value is greater
   * than 1, then the result is NaN.
   * <li>If the argument is zero, then the result is a zero with the
   * same sign as the argument.</ul>
   *
   * <p>The computed result must be within 1 ulp of the exact result.
   * Results must be semi-monotonic.
   *
   * @param   a   the value whose arc sine is to be returned.
   * @return  the arc sine of the argument.
   */
  public static double asin(double a) {
    return StrictMath.asin(a); // default impl. delegates to StrictMath
  }

  /**
   * Returns the arc cosine of a value; the returned angle is in the
   * range 0.0 through <i>pi</i>.  Special case:
   * <ul><li>If the argument is NaN or its absolute value is greater
   * than 1, then the result is NaN.</ul>
   *
   * <p>The computed result must be within 1 ulp of the exact result.
   * Results must be semi-monotonic.
   *
   * @param   a   the value whose arc cosine is to be returned.
   * @return  the arc cosine of the argument.
   */
  public static double acos(double a) {
    return StrictMath.acos(a); // default impl. delegates to StrictMath
  }

  /**
   * Returns the arc tangent of a value; the returned angle is in the
   * range -<i>pi</i>/2 through <i>pi</i>/2.  Special cases:
   * <ul><li>If the argument is NaN, then the result is NaN.
   * <li>If the argument is zero, then the result is a zero with the
   * same sign as the argument.</ul>
   *
   * <p>The computed result must be within 1 ulp of the exact result.
   * Results must be semi-monotonic.
   *
   * @param   a   the value whose arc tangent is to be returned.
   * @return  the arc tangent of the argument.
   */
  public static double atan(double a) {
    return StrictMath.atan(a); // default impl. delegates to StrictMath
  }

  /**
   * Converts an angle measured in degrees to an approximately
   * equivalent angle measured in radians.  The conversion from
   * degrees to radians is generally inexact.
   *
   * @param   angdeg   an angle, in degrees
   * @return  the measurement of the angle <code>angdeg</code>
   *          in radians.
   * @since   1.2
   */
  public static double toRadians(double angdeg) {
    return angdeg / 180.0 * PI;
  }

  /**
   * Converts an angle measured in radians to an approximately
   * equivalent angle measured in degrees.  The conversion from
   * radians to degrees is generally inexact; users should
   * <i>not</i> expect <code>cos(toRadians(90.0))</code> to exactly
   * equal <code>0.0</code>.
   *
   * @param   angrad   an angle, in radians
   * @return  the measurement of the angle <code>angrad</code>
   *          in degrees.
   * @since   1.2
   */
  public static double toDegrees(double angrad) {
    return angrad * 180.0 / PI;
  }

  /**
   * Returns Euler's number <i>e</i> raised to the power of a
   * <code>double</code> value.  Special cases:
   * <ul><li>If the argument is NaN, the result is NaN.
   * <li>If the argument is positive infinity, then the result is
   * positive infinity.
   * <li>If the argument is negative infinity, then the result is
   * positive zero.</ul>
   *
   * <p>The computed result must be within 1 ulp of the exact result.
   * Results must be semi-monotonic.
   *
   * @param   a   the exponent to raise <i>e</i> to.
   * @return  the value <i>e</i><sup><code>a</code></sup>,
   *          where <i>e</i> is the base of the natural logarithms.
   */
  public static double exp(double a) {
    return StrictMath.exp(a); // default impl. delegates to StrictMath
  }

  /**
   * Returns the natural logarithm (base <i>e</i>) of a <code>double</code>
   * value.  Special cases:
   * <ul><li>If the argument is NaN or less than zero, then the result
   * is NaN.
   * <li>If the argument is positive infinity, then the result is
   * positive infinity.
   * <li>If the argument is positive zero or negative zero, then the
   * result is negative infinity.</ul>
   *
   * <p>The computed result must be within 1 ulp of the exact result.
   * Results must be semi-monotonic.
   *
   * @param   a   a value
   * @return  the value ln&nbsp;<code>a</code>, the natural logarithm of
   *          <code>a</code>.
   */
  public static double log(double a) {
    return StrictMath.log(a); // default impl. delegates to StrictMath
  }

  /**
   * Returns the base 10 logarithm of a <code>double</code> value.
   * Special cases:
   *
   * <ul><li>If the argument is NaN or less than zero, then the result
   * is NaN.
   * <li>If the argument is positive infinity, then the result is
   * positive infinity.
   * <li>If the argument is positive zero or negative zero, then the
   * result is negative infinity.
   * <li> If the argument is equal to 10<sup><i>n</i></sup> for
   * integer <i>n</i>, then the result is <i>n</i>.
   * </ul>
   *
   * <p>The computed result must be within 1 ulp of the exact result.
   * Results must be semi-monotonic.
   *
   * @param   a   a value
   * @return  the base 10 logarithm of  <code>a</code>.
   * @since 1.5
   */
  public static double log10(double a) {
    return StrictMath.log10(a); // default impl. delegates to StrictMath
  }

  /**
   * Returns the correctly rounded positive square root of a
   * <code>double</code> value.
   * Special cases:
   * <ul><li>If the argument is NaN or less than zero, then the result
   * is NaN.
   * <li>If the argument is positive infinity, then the result is positive
   * infinity.
   * <li>If the argument is positive zero or negative zero, then the
   * result is the same as the argument.</ul>
   * Otherwise, the result is the <code>double</code> value closest to
   * the true mathematical square root of the argument value.
   *
   * @param   a   a value.
   * @return  the positive square root of <code>a</code>.
   *          If the argument is NaN or less than zero, the result is NaN.
   */
  public static double sqrt(double a) {
    return StrictMath.sqrt(a); // default impl. delegates to StrictMath
    // Note that hardware sqrt instructions
    // frequently can be directly used by JITs
    // and should be much faster than doing
    // Math.sqrt in software.
  }


  /**
   * Returns the cube root of a <code>double</code> value.  For
   * positive finite <code>x</code>, <code>cbrt(-x) ==
   * -cbrt(x)</code>; that is, the cube root of a negative value is
   * the negative of the cube root of that value's magnitude.
   *
   * Special cases:
   *
   * <ul>
   *
   * <li>If the argument is NaN, then the result is NaN.
   *
   * <li>If the argument is infinite, then the result is an infinity
   * with the same sign as the argument.
   *
   * <li>If the argument is zero, then the result is a zero with the
   * same sign as the argument.
   *
   * </ul>
   *
   * <p>The computed result must be within 1 ulp of the exact result.
   *
   * @param   a   a value.
   * @return  the cube root of <code>a</code>.
   * @since 1.5
   */
  public static double cbrt(double a) {
    return StrictMath.cbrt(a);
  }

  /**
   * Computes the remainder operation on two arguments as prescribed
   * by the IEEE 754 standard.
   * The remainder value is mathematically equal to
   * <code>f1&nbsp;-&nbsp;f2</code>&nbsp;&times;&nbsp;<i>n</i>,
   * where <i>n</i> is the mathematical integer closest to the exact
   * mathematical value of the quotient <code>f1/f2</code>, and if two
   * mathematical integers are equally close to <code>f1/f2</code>,
   * then <i>n</i> is the integer that is even. If the remainder is
   * zero, its sign is the same as the sign of the first argument.
   * Special cases:
   * <ul><li>If either argument is NaN, or the first argument is infinite,
   * or the second argument is positive zero or negative zero, then the
   * result is NaN.
   * <li>If the first argument is finite and the second argument is
   * infinite, then the result is the same as the first argument.</ul>
   *
   * @param   f1   the dividend.
   * @param   f2   the divisor.
   * @return  the remainder when <code>f1</code> is divided by
   *          <code>f2</code>.
   */
  public static double IEEEremainder(double f1, double f2) {
    return StrictMath.IEEEremainder(f1, f2); // delegate to StrictMath
  }

  /**
   * Returns the smallest (closest to negative infinity)
   * <code>double</code> value that is greater than or equal to the
   * argument and is equal to a mathematical integer. Special cases:
   * <ul><li>If the argument value is already equal to a
   * mathematical integer, then the result is the same as the
   * argument.  <li>If the argument is NaN or an infinity or
   * positive zero or negative zero, then the result is the same as
   * the argument.  <li>If the argument value is less than zero but
   * greater than -1.0, then the result is negative zero.</ul> Note
   * that the value of <code>Math.ceil(x)</code> is exactly the
   * value of <code>-Math.floor(-x)</code>.
   *
   *
   * @param   a   a value.
   * @return  the smallest (closest to negative infinity)
   *          floating-point value that is greater than or equal to
   *          the argument and is equal to a mathematical integer.
   */
  public static double ceil(double a) {
    return StrictMath.ceil(a); // default impl. delegates to StrictMath
  }

  /**
   * Returns the largest (closest to positive infinity)
   * <code>double</code> value that is less than or equal to the
   * argument and is equal to a mathematical integer. Special cases:
   * <ul><li>If the argument value is already equal to a
   * mathematical integer, then the result is the same as the
   * argument.  <li>If the argument is NaN or an infinity or
   * positive zero or negative zero, then the result is the same as
   * the argument.</ul>
   *
   * @param   a   a value.
   * @return  the largest (closest to positive infinity)
   *          floating-point value that less than or equal to the argument
   *          and is equal to a mathematical integer.
   */
  public static double floor(double a) {
    return StrictMath.floor(a); // default impl. delegates to StrictMath
  }

  /**
   * Returns the <code>double</code> value that is closest in value
   * to the argument and is equal to a mathematical integer. If two
   * <code>double</code> values that are mathematical integers are
   * equally close, the result is the integer value that is
   * even. Special cases:
   * <ul><li>If the argument value is already equal to a mathematical
   * integer, then the result is the same as the argument.
   * <li>If the argument is NaN or an infinity or positive zero or negative
   * zero, then the result is the same as the argument.</ul>
   *
   * @param   a   a <code>double</code> value.
   * @return  the closest floating-point value to <code>a</code> that is
   *          equal to a mathematical integer.
   */
  public static double rint(double a) {
    return StrictMath.rint(a); // default impl. delegates to StrictMath
  }

  /**
   * Returns the angle <i>theta</i> from the conversion of rectangular
   * coordinates (<code>x</code>,&nbsp;<code>y</code>) to polar
   * coordinates (r,&nbsp;<i>theta</i>).
   * This method computes the phase <i>theta</i> by computing an arc tangent
   * of <code>y/x</code> in the range of -<i>pi</i> to <i>pi</i>. Special
   * cases:
   * <ul><li>If either argument is NaN, then the result is NaN.
   * <li>If the first argument is positive zero and the second argument
   * is positive, or the first argument is positive and finite and the
   * second argument is positive infinity, then the result is positive
   * zero.
   * <li>If the first argument is negative zero and the second argument
   * is positive, or the first argument is negative and finite and the
   * second argument is positive infinity, then the result is negative zero.
   * <li>If the first argument is positive zero and the second argument
   * is negative, or the first argument is positive and finite and the
   * second argument is negative infinity, then the result is the
   * <code>double</code> value closest to <i>pi</i>.
   * <li>If the first argument is negative zero and the second argument
   * is negative, or the first argument is negative and finite and the
   * second argument is negative infinity, then the result is the
   * <code>double</code> value closest to -<i>pi</i>.
   * <li>If the first argument is positive and the second argument is
   * positive zero or negative zero, or the first argument is positive
   * infinity and the second argument is finite, then the result is the
   * <code>double</code> value closest to <i>pi</i>/2.
   * <li>If the first argument is negative and the second argument is
   * positive zero or negative zero, or the first argument is negative
   * infinity and the second argument is finite, then the result is the
   * <code>double</code> value closest to -<i>pi</i>/2.
   * <li>If both arguments are positive infinity, then the result is the
   * <code>double</code> value closest to <i>pi</i>/4.
   * <li>If the first argument is positive infinity and the second argument
   * is negative infinity, then the result is the <code>double</code>
   * value closest to 3*<i>pi</i>/4.
   * <li>If the first argument is negative infinity and the second argument
   * is positive infinity, then the result is the <code>double</code> value
   * closest to -<i>pi</i>/4.
   * <li>If both arguments are negative infinity, then the result is the
   * <code>double</code> value closest to -3*<i>pi</i>/4.</ul>
   *
   * <p>The computed result must be within 2 ulps of the exact result.
   * Results must be semi-monotonic.
   *
   * @param   y   the ordinate coordinate
   * @param   x   the abscissa coordinate
   * @return  the <i>theta</i> component of the point
   *          (<i>r</i>,&nbsp;<i>theta</i>)
   *          in polar coordinates that corresponds to the point
   *          (<i>x</i>,&nbsp;<i>y</i>) in Cartesian coordinates.
   */
  public static double atan2(double y, double x) {
    return StrictMath.atan2(y, x); // default impl. delegates to StrictMath
  }

  /**
   * Returns the value of the first argument raised to the power of the
   * second argument. Special cases:
   *
   * <ul><li>If the second argument is positive or negative zero, then the
   * result is 1.0.
   * <li>If the second argument is 1.0, then the result is the same as the
   * first argument.
   * <li>If the second argument is NaN, then the result is NaN.
   * <li>If the first argument is NaN and the second argument is nonzero,
   * then the result is NaN.
   *
   * <li>If
   * <ul>
   * <li>the absolute value of the first argument is greater than 1
   * and the second argument is positive infinity, or
   * <li>the absolute value of the first argument is less than 1 and
   * the second argument is negative infinity,
   * </ul>
   * then the result is positive infinity.
   *
   * <li>If
   * <ul>
   * <li>the absolute value of the first argument is greater than 1 and
   * the second argument is negative infinity, or
   * <li>the absolute value of the
   * first argument is less than 1 and the second argument is positive
   * infinity,
   * </ul>
   * then the result is positive zero.
   *
   * <li>If the absolute value of the first argument equals 1 and the
   * second argument is infinite, then the result is NaN.
   *
   * <li>If
   * <ul>
   * <li>the first argument is positive zero and the second argument
   * is greater than zero, or
   * <li>the first argument is positive infinity and the second
   * argument is less than zero,
   * </ul>
   * then the result is positive zero.
   *
   * <li>If
   * <ul>
   * <li>the first argument is positive zero and the second argument
   * is less than zero, or
   * <li>the first argument is positive infinity and the second
   * argument is greater than zero,
   * </ul>
   * then the result is positive infinity.
   *
   * <li>If
   * <ul>
   * <li>the first argument is negative zero and the second argument
   * is greater than zero but not a finite odd integer, or
   * <li>the first argument is negative infinity and the second
   * argument is less than zero but not a finite odd integer,
   * </ul>
   * then the result is positive zero.
   *
   * <li>If
   * <ul>
   * <li>the first argument is negative zero and the second argument
   * is a positive finite odd integer, or
   * <li>the first argument is negative infinity and the second
   * argument is a negative finite odd integer,
   * </ul>
   * then the result is negative zero.
   *
   * <li>If
   * <ul>
   * <li>the first argument is negative zero and the second argument
   * is less than zero but not a finite odd integer, or
   * <li>the first argument is negative infinity and the second
   * argument is greater than zero but not a finite odd integer,
   * </ul>
   * then the result is positive infinity.
   *
   * <li>If
   * <ul>
   * <li>the first argument is negative zero and the second argument
   * is a negative finite odd integer, or
   * <li>the first argument is negative infinity and the second
   * argument is a positive finite odd integer,
   * </ul>
   * then the result is negative infinity.
   *
   * <li>If the first argument is finite and less than zero
   * <ul>
   * <li> if the second argument is a finite even integer, the
   * result is equal to the result of raising the absolute value of
   * the first argument to the power of the second argument
   *
   * <li>if the second argument is a finite odd integer, the result
   * is equal to the negative of the result of raising the absolute
   * value of the first argument to the power of the second
   * argument
   *
   * <li>if the second argument is finite and not an integer, then
   * the result is NaN.
   * </ul>
   *
   * <li>If both arguments are integers, then the result is exactly equal
   * to the mathematical result of raising the first argument to the power
   * of the second argument if that result can in fact be represented
   * exactly as a <code>double</code> value.</ul>
   *
   * <p>(In the foregoing descriptions, a floating-point value is
   * considered to be an integer if and only if it is finite and a
   * fixed point of the method {@link #ceil <tt>ceil</tt>} or,
   * equivalently, a fixed point of the method {@link #floor
   * <tt>floor</tt>}. A value is a fixed point of a one-argument
   * method if and only if the result of applying the method to the
   * value is equal to the value.)
   *
   * <p>The computed result must be within 1 ulp of the exact result.
   * Results must be semi-monotonic.
   *
   * @param   a   the base.
   * @param   b   the exponent.
   * @return  the value <code>a<sup>b</sup></code>.
   */
  public static double pow(double a, double b) {
    return StrictMath.pow(a, b); // default impl. delegates to StrictMath
  }

  /**
   * Returns the closest <code>int</code> to the argument. The
   * result is rounded to an integer by adding 1/2, taking the
   * floor of the result, and casting the result to type <code>int</code>.
   * In other words, the result is equal to the value of the expression:
   * <p><pre>(int)Math.floor(a + 0.5f)</pre>
   * <p>
   * Special cases:
   * <ul><li>If the argument is NaN, the result is 0.
   * <li>If the argument is negative infinity or any value less than or
   * equal to the value of <code>Integer.MIN_VALUE</code>, the result is
   * equal to the value of <code>Integer.MIN_VALUE</code>.
   * <li>If the argument is positive infinity or any value greater than or
   * equal to the value of <code>Integer.MAX_VALUE</code>, the result is
   * equal to the value of <code>Integer.MAX_VALUE</code>.</ul>
   *
   * @param   a   a floating-point value to be rounded to an integer.
   * @return  the value of the argument rounded to the nearest
   *          <code>int</code> value.
   * @see     java.lang.Integer#MAX_VALUE
   * @see     java.lang.Integer#MIN_VALUE
   */
  public static int round(float a) {
    return (int)floor(a + 0.5f);
  }

  /**
   * Returns the closest <code>long</code> to the argument. The result
   * is rounded to an integer by adding 1/2, taking the floor of the
   * result, and casting the result to type <code>long</code>. In other
   * words, the result is equal to the value of the expression:
   * <p><pre>(long)Math.floor(a + 0.5d)</pre>
   * <p>
   * Special cases:
   * <ul><li>If the argument is NaN, the result is 0.
   * <li>If the argument is negative infinity or any value less than or
   * equal to the value of <code>Long.MIN_VALUE</code>, the result is
   * equal to the value of <code>Long.MIN_VALUE</code>.
   * <li>If the argument is positive infinity or any value greater than or
   * equal to the value of <code>Long.MAX_VALUE</code>, the result is
   * equal to the value of <code>Long.MAX_VALUE</code>.</ul>
   *
   * @param   a   a floating-point value to be rounded to a
   *		<code>long</code>.
   * @return  the value of the argument rounded to the nearest
   *          <code>long</code> value.
   * @see     java.lang.Long#MAX_VALUE
   * @see     java.lang.Long#MIN_VALUE
   */
  public static long round(double a) {
    return (long)floor(a + 0.5d);
  }

  private static Random randomNumberGenerator;

  private static synchronized void initRNG() {
    if (randomNumberGenerator == null)
      randomNumberGenerator = new Random();
  }

  /**
   * Returns a <code>double</code> value with a positive sign, greater
   * than or equal to <code>0.0</code> and less than <code>1.0</code>.
   * Returned values are chosen pseudorandomly with (approximately)
   * uniform distribution from that range.
   *
   * <p>When this method is first called, it creates a single new
   * pseudorandom-number generator, exactly as if by the expression
   * <blockquote><pre>new java.util.Random</pre></blockquote> This
   * new pseudorandom-number generator is used thereafter for all
   * calls to this method and is used nowhere else.
   *
   * <p>This method is properly synchronized to allow correct use by
   * more than one thread. However, if many threads need to generate
   * pseudorandom numbers at a great rate, it may reduce contention
   * for each thread to have its own pseudorandom-number generator.
   *
   * @return  a pseudorandom <code>double</code> greater than or equal
   * to <code>0.0</code> and less than <code>1.0</code>.
   * @see     java.util.Random#nextDouble()
   */
  public static double random() {
    if (randomNumberGenerator == null) initRNG();
    return randomNumberGenerator.nextDouble();
  }

  /**
   * Returns the absolute value of an <code>int</code> value.
   * If the argument is not negative, the argument is returned.
   * If the argument is negative, the negation of the argument is returned.
   *
   * <p>Note that if the argument is equal to the value of
   * <code>Integer.MIN_VALUE</code>, the most negative representable
   * <code>int</code> value, the result is that same value, which is
   * negative.
   *
   * @param   a   the argument whose absolute value is to be determined
   * @return  the absolute value of the argument.
   * @see     java.lang.Integer#MIN_VALUE
   */
  public static int abs(int a) {
    return (a < 0) ? -a : a;
  }

  /**
   * Returns the absolute value of a <code>long</code> value.
   * If the argument is not negative, the argument is returned.
   * If the argument is negative, the negation of the argument is returned.
   *
   * <p>Note that if the argument is equal to the value of
   * <code>Long.MIN_VALUE</code>, the most negative representable
   * <code>long</code> value, the result is that same value, which
   * is negative.
   *
   * @param   a   the argument whose absolute value is to be determined
   * @return  the absolute value of the argument.
   * @see     java.lang.Long#MIN_VALUE
   */
  public static long abs(long a) {
    return (a < 0) ? -a : a;
  }

  /**
   * Returns the absolute value of a <code>float</code> value.
   * If the argument is not negative, the argument is returned.
   * If the argument is negative, the negation of the argument is returned.
   * Special cases:
   * <ul><li>If the argument is positive zero or negative zero, the
   * result is positive zero.
   * <li>If the argument is infinite, the result is positive infinity.
   * <li>If the argument is NaN, the result is NaN.</ul>
   * In other words, the result is the same as the value of the expression:
   * <p><pre>Float.intBitsToFloat(0x7fffffff & Float.floatToIntBits(a))</pre>
   *
   * @param   a   the argument whose absolute value is to be determined
   * @return  the absolute value of the argument.
   */
  public static float abs(float a) {
    return (a <= 0.0F) ? 0.0F - a : a;
  }

  /**
   * Returns the absolute value of a <code>double</code> value.
   * If the argument is not negative, the argument is returned.
   * If the argument is negative, the negation of the argument is returned.
   * Special cases:
   * <ul><li>If the argument is positive zero or negative zero, the result
   * is positive zero.
   * <li>If the argument is infinite, the result is positive infinity.
   * <li>If the argument is NaN, the result is NaN.</ul>
   * In other words, the result is the same as the value of the expression:
   * <p><code>Double.longBitsToDouble((Double.doubleToLongBits(a)&lt;&lt;1)&gt;&gt;&gt;1)</code>
   *
   * @param   a   the argument whose absolute value is to be determined
   * @return  the absolute value of the argument.
   */
  public static double abs(double a) {
    return (a <= 0.0D) ? 0.0D - a : a;
  }

  /**
   * Returns the greater of two <code>int</code> values. That is, the
   * result is the argument closer to the value of
   * <code>Integer.MAX_VALUE</code>. If the arguments have the same value,
   * the result is that same value.
   *
   * @param   a   an argument.
   * @param   b   another argument.
   * @return  the larger of <code>a</code> and <code>b</code>.
   * @see     java.lang.Long#MAX_VALUE
   */
  public static int max(int a, int b) {
    return (a >= b) ? a : b;
  }

  /**
   * Returns the greater of two <code>long</code> values. That is, the
   * result is the argument closer to the value of
   * <code>Long.MAX_VALUE</code>. If the arguments have the same value,
   * the result is that same value.
   *
   * @param   a   an argument.
   * @param   b   another argument.
   * @return  the larger of <code>a</code> and <code>b</code>.
   * @see     java.lang.Long#MAX_VALUE
   */
  public static long max(long a, long b) {
    return (a >= b) ? a : b;
  }

  private static long negativeZeroFloatBits = Float.floatToIntBits(-0.0f);
  private static long negativeZeroDoubleBits = Double.doubleToLongBits(-0.0d);

  /**
   * Returns the greater of two <code>float</code> values.  That is,
   * the result is the argument closer to positive infinity. If the
   * arguments have the same value, the result is that same
   * value. If either value is NaN, then the result is NaN.  Unlike
   * the numerical comparison operators, this method considers
   * negative zero to be strictly smaller than positive zero. If one
   * argument is positive zero and the other negative zero, the
   * result is positive zero.
   *
   * @param   a   an argument.
   * @param   b   another argument.
   * @return  the larger of <code>a</code> and <code>b</code>.
   */
  public static float max(float a, float b) {
    if (a != a) return a;	// a is NaN
    if ((a == 0.0f) && (b == 0.0f)
        && (Float.floatToIntBits(a) == negativeZeroFloatBits)) {
      return b;
    }
    return (a >= b) ? a : b;
  }

  /**
   * Returns the greater of two <code>double</code> values.  That
   * is, the result is the argument closer to positive infinity. If
   * the arguments have the same value, the result is that same
   * value. If either value is NaN, then the result is NaN.  Unlike
   * the numerical comparison operators, this method considers
   * negative zero to be strictly smaller than positive zero. If one
   * argument is positive zero and the other negative zero, the
   * result is positive zero.
   *
   * @param   a   an argument.
   * @param   b   another argument.
   * @return  the larger of <code>a</code> and <code>b</code>.
   */
  public static double max(double a, double b) {
    if (a != a) return a;	// a is NaN
    if ((a == 0.0d) && (b == 0.0d)
        && (Double.doubleToLongBits(a) == negativeZeroDoubleBits)) {
      return b;
    }
    return (a >= b) ? a : b;
  }

  /**
   * Returns the smaller of two <code>int</code> values. That is,
   * the result the argument closer to the value of
   * <code>Integer.MIN_VALUE</code>.  If the arguments have the same
   * value, the result is that same value.
   *
   * @param   a   an argument.
   * @param   b   another argument.
   * @return  the smaller of <code>a</code> and <code>b</code>.
   * @see     java.lang.Long#MIN_VALUE
   */
  public static int min(int a, int b) {
    return (a <= b) ? a : b;
  }

  /**
   * Returns the smaller of two <code>long</code> values. That is,
   * the result is the argument closer to the value of
   * <code>Long.MIN_VALUE</code>. If the arguments have the same
   * value, the result is that same value.
   *
   * @param   a   an argument.
   * @param   b   another argument.
   * @return  the smaller of <code>a</code> and <code>b</code>.
   * @see     java.lang.Long#MIN_VALUE
   */
  public static long min(long a, long b) {
    return (a <= b) ? a : b;
  }

  /**
   * Returns the smaller of two <code>float</code> values.  That is,
   * the result is the value closer to negative infinity. If the
   * arguments have the same value, the result is that same
   * value. If either value is NaN, then the result is NaN.  Unlike
   * the numerical comparison operators, this method considers
   * negative zero to be strictly smaller than positive zero.  If
   * one argument is positive zero and the other is negative zero,
   * the result is negative zero.
   *
   * @param   a   an argument.
   * @param   b   another argument.
   * @return  the smaller of <code>a</code> and <code>b.</code>
   */
  public static float min(float a, float b) {
    if (a != a) return a;	// a is NaN
    if ((a == 0.0f) && (b == 0.0f)
        && (Float.floatToIntBits(b) == negativeZeroFloatBits)) {
      return b;
    }
    return (a <= b) ? a : b;
  }

  /**
   * Returns the smaller of two <code>double</code> values.  That
   * is, the result is the value closer to negative infinity. If the
   * arguments have the same value, the result is that same
   * value. If either value is NaN, then the result is NaN.  Unlike
   * the numerical comparison operators, this method considers
   * negative zero to be strictly smaller than positive zero. If one
   * argument is positive zero and the other is negative zero, the
   * result is negative zero.
   *
   * @param   a   an argument.
   * @param   b   another argument.
   * @return  the smaller of <code>a</code> and <code>b</code>.
   */
  public static double min(double a, double b) {
    if (a != a) return a;	// a is NaN
    if ((a == 0.0d) && (b == 0.0d)
        && (Double.doubleToLongBits(b) == negativeZeroDoubleBits)) {
      return b;
    }
    return (a <= b) ? a : b;
  }

  /**
   * Returns the size of an ulp of the argument.  An ulp of a
   * <code>double</code> value is the positive distance between this
   * floating-point value and the <code>double</code> value next
   * larger in magnitude.  Note that for non-NaN <i>x</i>,
   * <code>ulp(-<i>x</i>) == ulp(<i>x</i>)</code>.
   *
   * <p>Special Cases:
   * <ul>
   * <li> If the argument is NaN, then the result is NaN.
   * <li> If the argument is positive or negative infinity, then the
   * result is positive infinity.
   * <li> If the argument is positive or negative zero, then the result is
   * <code>Double.MIN_VALUE</code>.
   * <li> If the argument is &plusmn;<code>Double.MAX_VALUE</code>, then
   * the result is equal to 2<sup>971</sup>.
   * </ul>
   *
   * @param d the floating-point value whose ulp is to be returned
   * @return the size of an ulp of the argument
   * @author Joseph D. Darcy
   * @since 1.5
   */
//  public static double ulp(double d) {
//    return sun.misc.FpUtils.ulp(d);
//  }


  /**
   * Returns the size of an ulp of the argument.  An ulp of a
   * <code>float</code> value is the positive distance between this
   * floating-point value and the <code>float</code> value next
   * larger in magnitude.  Note that for non-NaN <i>x</i>,
   * <code>ulp(-<i>x</i>) == ulp(<i>x</i>)</code>.
   *
   * <p>Special Cases:
   * <ul>
   * <li> If the argument is NaN, then the result is NaN.
   * <li> If the argument is positive or negative infinity, then the
   * result is positive infinity.
   * <li> If the argument is positive or negative zero, then the result is
   * <code>Float.MIN_VALUE</code>.
   * <li> If the argument is &plusmn;<code>Float.MAX_VALUE</code>, then
   * the result is equal to 2<sup>104</sup>.
   * </ul>
   *
   * @param f the floating-point value whose ulp is to be returned
   * @return the size of an ulp of the argument
   * @author Joseph D. Darcy
   * @since 1.5
   */
  public static float ulp(float f) {
    return sun.misc.FpUtils.ulp(f);
  }

//  /**
//   * Returns the signum function of the argument; zero if the argument
//   * is zero, 1.0 if the argument is greater than zero, -1.0 if the
//   * argument is less than zero.
//   *
//   * <p>Special Cases:
//   * <ul>
//   * <li> If the argument is NaN, then the result is NaN.
//   * <li> If the argument is positive zero or negative zero, then the
//   *      result is the same as the argument.
//   * </ul>
//   *
//   * @param d the floating-point value whose signum is to be returned
//   * @return the signum function of the argument
//   * @author Joseph D. Darcy
//   * @since 1.5
//   */
//  public static double signum(double d) {
//    return sun.misc.FpUtils.signum(d);
//  }

  /**
   * Returns the signum function of the argument; zero if the argument
   * is zero, 1.0f if the argument is greater than zero, -1.0f if the
   * argument is less than zero.
   *
   * <p>Special Cases:
   * <ul>
   * <li> If the argument is NaN, then the result is NaN.
   * <li> If the argument is positive zero or negative zero, then the
   *      result is the same as the argument.
   * </ul>
   *
   * @param f the floating-point value whose signum is to be returned
   * @return the signum function of the argument
   * @author Joseph D. Darcy
   * @since 1.5
   */
  public static float signum(float f) {
    return sun.misc.FpUtils.signum(f);
  }

  /**
   * Returns the hyperbolic sine of a <code>double</code> value.
   * The hyperbolic sine of <i>x</i> is defined to be
   * (<i>e<sup>x</sup>&nbsp;-&nbsp;e<sup>-x</sup></i>)/2
   * where <i>e</i> is {@linkplain Math#E Euler's number}.
   *
   * <p>Special cases:
   * <ul>
   *
   * <li>If the argument is NaN, then the result is NaN.
   *
   * <li>If the argument is infinite, then the result is an infinity
   * with the same sign as the argument.
   *
   * <li>If the argument is zero, then the result is a zero with the
   * same sign as the argument.
   *
   * </ul>
   *
   * <p>The computed result must be within 2.5 ulps of the exact result.
   *
   * @param   x The number whose hyperbolic sine is to be returned.
   * @return  The hyperbolic sine of <code>x</code>.
   * @since 1.5
   */
  public static double sinh(double x) {
    return StrictMath.sinh(x);
  }

  /**
   * Returns the hyperbolic cosine of a <code>double</code> value.
   * The hyperbolic cosine of <i>x</i> is defined to be
   * (<i>e<sup>x</sup>&nbsp;+&nbsp;e<sup>-x</sup></i>)/2
   * where <i>e</i> is {@linkplain Math#E Euler's number}.
   *
   * <p>Special cases:
   * <ul>
   *
   * <li>If the argument is NaN, then the result is NaN.
   *
   * <li>If the argument is infinite, then the result is positive
   * infinity.
   *
   * <li>If the argument is zero, then the result is <code>1.0</code>.
   *
   * </ul>
   *
   * <p>The computed result must be within 2.5 ulps of the exact result.
   *
   * @param   x The number whose hyperbolic cosine is to be returned.
   * @return  The hyperbolic cosine of <code>x</code>.
   * @since 1.5
   */
  public static double cosh(double x) {
    return StrictMath.cosh(x);
  }

  /**
   * Returns the hyperbolic tangent of a <code>double</code> value.
   * The hyperbolic tangent of <i>x</i> is defined to be
   * (<i>e<sup>x</sup>&nbsp;-&nbsp;e<sup>-x</sup></i>)/(<i>e<sup>x</sup>&nbsp;+&nbsp;e<sup>-x</sup></i>),
   * in other words, {@linkplain Math#sinh
   * sinh(<i>x</i>)}/{@linkplain Math#cosh cosh(<i>x</i>)}.  Note
   * that the absolute value of the exact tanh is always less than
   * 1.
   *
   * <p>Special cases:
   * <ul>
   *
   * <li>If the argument is NaN, then the result is NaN.
   *
   * <li>If the argument is zero, then the result is a zero with the
   * same sign as the argument.
   *
   * <li>If the argument is positive infinity, then the result is
   * <code>+1.0</code>.
   *
   * <li>If the argument is negative infinity, then the result is
   * <code>-1.0</code>.
   *
   * </ul>
   *
   * <p>The computed result must be within 2.5 ulps of the exact result.
   * The result of <code>tanh</code> for any finite input must have
   * an absolute value less than or equal to 1.  Note that once the
   * exact result of tanh is within 1/2 of an ulp of the limit value
   * of &plusmn;1, correctly signed &plusmn;<code>1.0</code> should
   * be returned.
   *
   * @param   x The number whose hyperbolic tangent is to be returned.
   * @return  The hyperbolic tangent of <code>x</code>.
   * @since 1.5
   */
  public static double tanh(double x) {
    return StrictMath.tanh(x);
  }

  /**
   * Returns sqrt(<i>x</i><sup>2</sup>&nbsp;+<i>y</i><sup>2</sup>)
   * without intermediate overflow or underflow.
   *
   * <p>Special cases:
   * <ul>
   *
   * <li> If either argument is infinite, then the result
   * is positive infinity.
   *
   * <li> If either argument is NaN and neither argument is infinite,
   * then the result is NaN.
   *
   * </ul>
   *
   * <p>The computed result must be within 1 ulp of the exact
   * result.  If one parameter is held constant, the results must be
   * semi-monotonic in the other parameter.
   *
   * @param x a value
   * @param y a value
   * @return sqrt(<i>x</i><sup>2</sup>&nbsp;+<i>y</i><sup>2</sup>)
   * without intermediate overflow or underflow
   * @since 1.5
   */
  public static double hypot(double x, double y) {
    return StrictMath.hypot(x, y);
  }

  /**
   * Returns <i>e</i><sup>x</sup>&nbsp;-1.  Note that for values of
   * <i>x</i> near 0, the exact sum of
   * <code>expm1(x)</code>&nbsp;+&nbsp;1 is much closer to the true
   * result of <i>e</i><sup>x</sup> than <code>exp(x)</code>.
   *
   * <p>Special cases:
   * <ul>
   * <li>If the argument is NaN, the result is NaN.
   *
   * <li>If the argument is positive infinity, then the result is
   * positive infinity.
   *
   * <li>If the argument is negative infinity, then the result is
   * -1.0.
   *
   * <li>If the argument is zero, then the result is a zero with the
   * same sign as the argument.
   *
   * </ul>
   *
   * <p>The computed result must be within 1 ulp of the exact result.
   * Results must be semi-monotonic.  The result of
   * <code>expm1</code> for any finite input must be greater than or
   * equal to <code>-1.0</code>.  Note that once the exact result of
   * <i>e</i><sup><code>x</code></sup>&nbsp;-&nbsp;1 is within 1/2
   * ulp of the limit value -1, <code>-1.0</code> should be
   * returned.
   *
   * @param   x   the exponent to raise <i>e</i> to in the computation of
   *              <i>e</i><sup><code>x</code></sup>&nbsp;-1.
   * @return  the value <i>e</i><sup><code>x</code></sup>&nbsp;-&nbsp;1.
   * @since 1.5
   */
  public static double expm1(double x) {
    return StrictMath.expm1(x);
  }

  /**
   * Returns the natural logarithm of the sum of the argument and 1.
   * Note that for small values <code>x</code>, the result of
   * <code>log1p(x)</code> is much closer to the true result of ln(1
   * + <code>x</code>) than the floating-point evaluation of
   * <code>log(1.0+x)</code>.
   *
   * <p>Special cases:
   *
   * <ul>
   *
   * <li>If the argument is NaN or less than -1, then the result is
   * NaN.
   *
   * <li>If the argument is positive infinity, then the result is
   * positive infinity.
   *
   * <li>If the argument is negative one, then the result is
   * negative infinity.
   *
   * <li>If the argument is zero, then the result is a zero with the
   * same sign as the argument.
   *
   * </ul>
   *
   * <p>The computed result must be within 1 ulp of the exact result.
   * Results must be semi-monotonic.
   *
   * @param   x   a value
   * @return the value ln(<code>x</code>&nbsp;+&nbsp;1), the natural
   * log of <code>x</code>&nbsp;+&nbsp;1
   * @since 1.5
   */
  public static double log1p(double x) {
    return StrictMath.log1p(x);
  }

//  /**
//   * Returns the first floating-point argument with the sign of the
//   * second floating-point argument.  Note that unlike the {@link
//   * StrictMath#copySign(double, double) StrictMath.copySign}
//   * method, this method does not require NaN <code>sign</code>
//   * arguments to be treated as positive values; implementations are
//   * permitted to treat some NaN arguments as positive and other NaN
//   * arguments as negative to allow greater performance.
//   *
//   * @param magnitude  the parameter providing the magnitude of the result
//   * @param sign   the parameter providing the sign of the result
//   * @return a value with the magnitude of <code>magnitude</code>
//   * and the sign of <code>sign</code>.
//   * @since 1.6
//   */
//  public static double copySign(double magnitude, double sign) {
//    return sun.misc.FpUtils.rawCopySign(magnitude, sign);
//  }

  /**
   * Returns the first floating-point argument with the sign of the
   * second floating-point argument.  Note that unlike the {@link
   * StrictMath#copySign(float, float) StrictMath.copySign}
   * method, this method does not require NaN <code>sign</code>
   * arguments to be treated as positive values; implementations are
   * permitted to treat some NaN arguments as positive and other NaN
   * arguments as negative to allow greater performance.
   *
   * @param magnitude  the parameter providing the magnitude of the result
   * @param sign   the parameter providing the sign of the result
   * @return a value with the magnitude of <code>magnitude</code>
   * and the sign of <code>sign</code>.
   * @since 1.6
   */
  public static float copySign(float magnitude, float sign) {
    return sun.misc.FpUtils.rawCopySign(magnitude, sign);
  }

  /**
   * Returns the unbiased exponent used in the representation of a
   * {@code float}.  Special cases:
   *
   * <ul>
   * <li>If the argument is NaN or infinite, then the result is
   * {@link Float#MAX_EXPONENT} + 1.
   * <li>If the argument is zero or subnormal, then the result is
   * {@link Float#MIN_EXPONENT} -1.
   * </ul>
   * @param f a {@code float} value
   * @return the unbiased exponent of the argument
   * @since 1.6
   */
  public static int getExponent(float f) {
    return sun.misc.FpUtils.getExponent(f);
  }
//
//  /**
//   * Returns the unbiased exponent used in the representation of a
//   * {@code double}.  Special cases:
//   *
//   * <ul>
//   * <li>If the argument is NaN or infinite, then the result is
//   * {@link Double#MAX_EXPONENT} + 1.
//   * <li>If the argument is zero or subnormal, then the result is
//   * {@link Double#MIN_EXPONENT} -1.
//   * </ul>
//   * @param d a {@code double} value
//   * @return the unbiased exponent of the argument
//   * @since 1.6
//   */
//  public static int getExponent(double d) {
//    return sun.misc.FpUtils.getExponent(d);
//  }
//
//  /**
//   * Returns the floating-point number adjacent to the first
//   * argument in the direction of the second argument.  If both
//   * arguments compare as equal the second argument is returned.
//   *
//   * <p>
//   * Special cases:
//   * <ul>
//   * <li> If either argument is a NaN, then NaN is returned.
//   *
//   * <li> If both arguments are signed zeros, {@code direction}
//   * is returned unchanged (as implied by the requirement of
//   * returning the second argument if the arguments compare as
//   * equal).
//   *
//   * <li> If {@code start} is
//   * &plusmn;{@link Double#MIN_VALUE} and {@code direction}
//   * has a value such that the result should have a smaller
//   * magnitude, then a zero with the same sign as {@code start}
//   * is returned.
//   *
//   * <li> If {@code start} is infinite and
//   * {@code direction} has a value such that the result should
//   * have a smaller magnitude, {@link Double#MAX_VALUE} with the
//   * same sign as {@code start} is returned.
//   *
//   * <li> If {@code start} is equal to &plusmn;
//   * {@link Double#MAX_VALUE} and {@code direction} has a
//   * value such that the result should have a larger magnitude, an
//   * infinity with same sign as {@code start} is returned.
//   * </ul>
//   *
//   * @param start  starting floating-point value
//   * @param direction value indicating which of
//   * {@code start}'s neighbors or {@code start} should
//   * be returned
//   * @return The floating-point number adjacent to {@code start} in the
//   * direction of {@code direction}.
//   * @since 1.6
//   */
//  public static double nextAfter(double start, double direction) {
//    return sun.misc.FpUtils.nextAfter(start, direction);
//  }

  /**
   * Returns the floating-point number adjacent to the first
   * argument in the direction of the second argument.  If both
   * arguments compare as equal a value equivalent to the second argument
   * is returned.
   *
   * <p>
   * Special cases:
   * <ul>
   * <li> If either argument is a NaN, then NaN is returned.
   *
   * <li> If both arguments are signed zeros, a value equivalent
   * to {@code direction} is returned.
   *
   * <li> If {@code start} is
   * &plusmn;{@link Float#MIN_VALUE} and {@code direction}
   * has a value such that the result should have a smaller
   * magnitude, then a zero with the same sign as {@code start}
   * is returned.
   *
   * <li> If {@code start} is infinite and
   * {@code direction} has a value such that the result should
   * have a smaller magnitude, {@link Float#MAX_VALUE} with the
   * same sign as {@code start} is returned.
   *
   * <li> If {@code start} is equal to &plusmn;
   * {@link Float#MAX_VALUE} and {@code direction} has a
   * value such that the result should have a larger magnitude, an
   * infinity with same sign as {@code start} is returned.
   * </ul>
   *
   * @param start  starting floating-point value
   * @param direction value indicating which of
   * {@code start}'s neighbors or {@code start} should
   * be returned
   * @return The floating-point number adjacent to {@code start} in the
   * direction of {@code direction}.
   * @since 1.6
   */
  public static float nextAfter(float start, double direction) {
    return sun.misc.FpUtils.nextAfter(start, direction);
  }

//  /**
//   * Returns the floating-point value adjacent to {@code d} in
//   * the direction of positive infinity.  This method is
//   * semantically equivalent to {@code nextAfter(d,
//   * Double.POSITIVE_INFINITY)}; however, a {@code nextUp}
//   * implementation may run faster than its equivalent
//   * {@code nextAfter} call.
//   *
//   * <p>Special Cases:
//   * <ul>
//   * <li> If the argument is NaN, the result is NaN.
//   *
//   * <li> If the argument is positive infinity, the result is
//   * positive infinity.
//   *
//   * <li> If the argument is zero, the result is
//   * {@link Double#MIN_VALUE}
//   *
//   * </ul>
//   *
//   * @param d starting floating-point value
//   * @return The adjacent floating-point value closer to positive
//   * infinity.
//   * @since 1.6
//   */
//  public static double nextUp(double d) {
//    return sun.misc.FpUtils.nextUp(d);
//  }
//
  /**
   * Returns the floating-point value adjacent to {@code f} in
   * the direction of positive infinity.  This method is
   * semantically equivalent to {@code nextAfter(f,
   * Float.POSITIVE_INFINITY)}; however, a {@code nextUp}
   * implementation may run faster than its equivalent
   * {@code nextAfter} call.
   *
   * <p>Special Cases:
   * <ul>
   * <li> If the argument is NaN, the result is NaN.
   *
   * <li> If the argument is positive infinity, the result is
   * positive infinity.
   *
   * <li> If the argument is zero, the result is
   * {@link Float#MIN_VALUE}
   *
   * </ul>
   *
   * @param f starting floating-point value
   * @return The adjacent floating-point value closer to positive
   * infinity.
   * @since 1.6
   */
  public static float nextUp(float f) {
    return sun.misc.FpUtils.nextUp(f);
  }


  /**
   * Return {@code d} &times;
   * 2<sup>{@code scaleFactor}</sup> rounded as if performed
   * by a single correctly rounded floating-point multiply to a
   * member of the double value set.  See the Java
   * Language Specification for a discussion of floating-point
   * value sets.  If the exponent of the result is between {@link
   * Double#MIN_EXPONENT} and {@link Double#MAX_EXPONENT}, the
   * answer is calculated exactly.  If the exponent of the result
   * would be larger than {@code Double.MAX_EXPONENT}, an
   * infinity is returned.  Note that if the result is subnormal,
   * precision may be lost; that is, when {@code scalb(x, n)}
   * is subnormal, {@code scalb(scalb(x, n), -n)} may not equal
   * <i>x</i>.  When the result is non-NaN, the result has the same
   * sign as {@code d}.
   *
   *<p>
   * Special cases:
   * <ul>
   * <li> If the first argument is NaN, NaN is returned.
   * <li> If the first argument is infinite, then an infinity of the
   * same sign is returned.
   * <li> If the first argument is zero, then a zero of the same
   * sign is returned.
   * </ul>
   *
   * @param d number to be scaled by a power of two.
   * @param scaleFactor power of 2 used to scale {@code d}
   * @return {@code d} &times; 2<sup>{@code scaleFactor}</sup>
   * @since 1.6
   */
//  public static double scalb(double d, int scaleFactor) {
//    return sun.misc.FpUtils.scalb(d, scaleFactor);
//  }

  /**
   * Return {@code f} &times;
   * 2<sup>{@code scaleFactor}</sup> rounded as if performed
   * by a single correctly rounded floating-point multiply to a
   * member of the float value set.  See the Java
   * Language Specification for a discussion of floating-point
   * value sets.  If the exponent of the result is between {@link
   * Float#MIN_EXPONENT} and {@link Float#MAX_EXPONENT}, the
   * answer is calculated exactly.  If the exponent of the result
   * would be larger than {@code Float.MAX_EXPONENT}, an
   * infinity is returned.  Note that if the result is subnormal,
   * precision may be lost; that is, when {@code scalb(x, n)}
   * is subnormal, {@code scalb(scalb(x, n), -n)} may not equal
   * <i>x</i>.  When the result is non-NaN, the result has the same
   * sign as {@code f}.
   *
   *<p>
   * Special cases:
   * <ul>
   * <li> If the first argument is NaN, NaN is returned.
   * <li> If the first argument is infinite, then an infinity of the
   * same sign is returned.
   * <li> If the first argument is zero, then a zero of the same
   * sign is returned.
   * </ul>
   *
   * @param f number to be scaled by a power of two.
   * @param scaleFactor power of 2 used to scale {@code f}
   * @return {@code f} &times; 2<sup>{@code scaleFactor}</sup>
   * @since 1.6
   */
  public static float scalb(float f, int scaleFactor) {
    return sun.misc.FpUtils.scalb(f, scaleFactor);
  }
}
