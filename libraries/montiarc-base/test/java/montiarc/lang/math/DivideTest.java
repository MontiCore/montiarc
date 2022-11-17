/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang.math;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class DivideTest {

  @ParameterizedTest
  @MethodSource("divideIntegerTestProvider")
  public void testBehavior(int a, int b, int r) {
    // Given
    DivideI divide = new DivideI();
    divide.setUp();
    divide.init();
    divide.getA().update(a);
    divide.getB().update(b);

    // When
    divide.compute();

    // Then
    assertThat(divide.getR().getValue()).isEqualTo(r);
  }

  protected static Stream<Arguments> divideIntegerTestProvider() {
    return Stream.of(
      // Arguments.of(0, 0, NAN, false),
      Arguments.of(0, 1, 0, false),
      Arguments.of(0, -1, 0, false),
      Arguments.of(0, Integer.MAX_VALUE, 0, false),
      Arguments.of(0, Integer.MIN_VALUE, 0, false),
      // Arguments.of(1, 0, NAN, false),
      Arguments.of(1, 1, 1, false),
      Arguments.of(1, -1, -1, false),
      Arguments.of(1, Integer.MAX_VALUE, 0, false),
      Arguments.of(1, Integer.MIN_VALUE, 0, false),
      // Arguments.of(-1, 0, NAN, false),
      Arguments.of(-1, 1, -1, false),
      Arguments.of(-1, -1, 1, false),
      Arguments.of(-1, Integer.MAX_VALUE, 0, false),
      Arguments.of(-1, Integer.MIN_VALUE, 0, false),
      // Arguments.of(Integer.MAX_VALUE, 0, NAN, false),
      Arguments.of(Integer.MAX_VALUE, 1, Integer.MAX_VALUE, false),
      Arguments.of(Integer.MAX_VALUE, -1, -Integer.MAX_VALUE, false),
      Arguments.of(Integer.MAX_VALUE, Integer.MAX_VALUE, 1, false),
      Arguments.of(Integer.MAX_VALUE, Integer.MIN_VALUE, 0, false),
      // Arguments.of(Integer.MIN_VALUE, 0, NAN, false),
      Arguments.of(Integer.MIN_VALUE, 1, Integer.MIN_VALUE, false),
      Arguments.of(Integer.MIN_VALUE, -1, Integer.MIN_VALUE, true),
      Arguments.of(Integer.MIN_VALUE, Integer.MAX_VALUE, -1, false),
      Arguments.of(Integer.MIN_VALUE, Integer.MIN_VALUE, 1, false)
    );
  }

  @ParameterizedTest
  @MethodSource("divideLongTestProvider")
  public void testBehavior(long a, long b, long r) {
    // Given
    DivideL divide = new DivideL();
    divide.setUp();
    divide.init();
    divide.getA().update(a);
    divide.getB().update(b);

    // When
    divide.compute();

    // Then
    assertThat(divide.getR().getValue()).isEqualTo(r);
  }

  protected static Stream<Arguments> divideLongTestProvider() {
    return Stream.of(
      // Arguments.of(0, 0, NAN, false),
      Arguments.of(0, 1, 0, false),
      Arguments.of(0, -1, 0, false),
      Arguments.of(0, Long.MAX_VALUE, 0, false),
      Arguments.of(0, Long.MIN_VALUE, 0, false),
      // Arguments.of(1, 0, NAN, false),
      Arguments.of(1, 1, 1, false),
      Arguments.of(1, -1, -1, false),
      Arguments.of(1, Long.MAX_VALUE, 0, false),
      Arguments.of(1, Long.MIN_VALUE, 0, false),
      // Arguments.of(-1, 0, NAN, false),
      Arguments.of(-1, 1, -1, false),
      Arguments.of(-1, -1, 1, false),
      Arguments.of(-1, Long.MAX_VALUE, 0, false),
      Arguments.of(-1, Long.MIN_VALUE, 0, false),
      // Arguments.of(Long.MAX_VALUE, 0, NAN, false),
      Arguments.of(Long.MAX_VALUE, 1, Long.MAX_VALUE, false),
      Arguments.of(Long.MAX_VALUE, -1, -Long.MAX_VALUE, false),
      Arguments.of(Long.MAX_VALUE, Long.MAX_VALUE, 1, false),
      Arguments.of(Long.MAX_VALUE, Long.MIN_VALUE, 0, false),
      // Arguments.of(Long.MIN_VALUE, 0, NAN, false),
      Arguments.of(Long.MIN_VALUE, 1, Long.MIN_VALUE, false),
      Arguments.of(Long.MIN_VALUE, -1, Long.MIN_VALUE, true),
      Arguments.of(Long.MIN_VALUE, Long.MAX_VALUE, -1, false),
      Arguments.of(Long.MIN_VALUE, Long.MIN_VALUE, 1, false)
    );
  }
}
