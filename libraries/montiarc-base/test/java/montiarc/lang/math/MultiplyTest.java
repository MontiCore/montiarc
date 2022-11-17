/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang.math;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class MultiplyTest {

  @ParameterizedTest
  @MethodSource("multiplyIntegerTestProvider")
  public void testBehavior(int a, int b, int r, boolean of) {
    // Given
    MultiplyI multiply = new MultiplyI();
    multiply.setUp();
    multiply.init();
    multiply.getA().update(a);
    multiply.getB().update(b);

    // When
    multiply.compute();

    // Then
    assertAll(
      () -> assertThat(multiply.getR().getValue()).isEqualTo(r),
      () -> assertThat(multiply.getOf().getValue()).isEqualTo(of)
    );
  }

  protected static Stream<Arguments> multiplyIntegerTestProvider() {
    return Stream.of(
      Arguments.of(0, 0, 0, false),
      Arguments.of(0, 1, 0, false),
      Arguments.of(0, -1, 0, false),
      Arguments.of(0, Integer.MAX_VALUE, 0, false),
      Arguments.of(0, Integer.MIN_VALUE, 0, false),
      Arguments.of(1, 0, 0, false),
      Arguments.of(1, 1, 1, false),
      Arguments.of(1, -1, -1, false),
      Arguments.of(1, Integer.MAX_VALUE, Integer.MAX_VALUE, false),
      Arguments.of(1, Integer.MIN_VALUE, Integer.MIN_VALUE, false),
      Arguments.of(-1, 0, 0, false),
      Arguments.of(-1, 1, -1, false),
      Arguments.of(-1, -1, 1, false),
      Arguments.of(-1, Integer.MAX_VALUE, -Integer.MAX_VALUE, false),
      Arguments.of(-1, Integer.MIN_VALUE, Integer.MIN_VALUE, true),
      Arguments.of(Integer.MAX_VALUE, 0, 0, false),
      Arguments.of(Integer.MAX_VALUE, 1, Integer.MAX_VALUE, false),
      Arguments.of(Integer.MAX_VALUE, -1, -Integer.MAX_VALUE, false),
      Arguments.of(Integer.MAX_VALUE, Integer.MAX_VALUE, 1, true),
      Arguments.of(Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, true),
      Arguments.of(Integer.MIN_VALUE, 0, 0, false),
      Arguments.of(Integer.MIN_VALUE, 1, Integer.MIN_VALUE, false),
      Arguments.of(Integer.MIN_VALUE, -1, Integer.MIN_VALUE, true),
      Arguments.of(Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, true),
      Arguments.of(Integer.MIN_VALUE, Integer.MIN_VALUE, 0, true)
    );
  }

  @ParameterizedTest
  @MethodSource("multiplyLongTestProvider")
  public void testBehavior(long a, long b, long r, boolean of) {
    // Given
    MultiplyL multiply = new MultiplyL();
    multiply.setUp();
    multiply.init();
    multiply.getA().update(a);
    multiply.getB().update(b);

    // When
    multiply.compute();

    // Then
    assertAll(
      () -> assertThat(multiply.getR().getValue()).isEqualTo(r),
      () -> assertThat(multiply.getOf().getValue()).isEqualTo(of)
    );
  }

  protected static Stream<Arguments> multiplyLongTestProvider() {
    return Stream.of(
      Arguments.of(0, 0, 0, false),
      Arguments.of(0, 1, 0, false),
      Arguments.of(0, -1, 0, false),
      Arguments.of(0, Long.MAX_VALUE, 0, false),
      Arguments.of(0, Long.MIN_VALUE, 0, false),
      Arguments.of(1, 0, 0, false),
      Arguments.of(1, 1, 1, false),
      Arguments.of(1, -1, -1, false),
      Arguments.of(1, Long.MAX_VALUE, Long.MAX_VALUE, false),
      Arguments.of(1, Long.MIN_VALUE, Long.MIN_VALUE, false),
      Arguments.of(-1, 0, 0, false),
      Arguments.of(-1, 1, -1, false),
      Arguments.of(-1, -1, 1, false),
      Arguments.of(-1, Long.MAX_VALUE, -Long.MAX_VALUE, false),
      Arguments.of(-1, Long.MIN_VALUE, Long.MIN_VALUE, true),
      Arguments.of(Long.MAX_VALUE, 0, 0, false),
      Arguments.of(Long.MAX_VALUE, 1, Long.MAX_VALUE, false),
      Arguments.of(Long.MAX_VALUE, -1, -Long.MAX_VALUE, false),
      Arguments.of(Long.MAX_VALUE, Long.MAX_VALUE, 1, true),
      Arguments.of(Long.MAX_VALUE, Long.MIN_VALUE, Long.MIN_VALUE, true),
      Arguments.of(Long.MIN_VALUE, 0, 0, false),
      Arguments.of(Long.MIN_VALUE, 1, Long.MIN_VALUE, false),
      Arguments.of(Long.MIN_VALUE, -1, Long.MIN_VALUE, true),
      Arguments.of(Long.MIN_VALUE, Long.MAX_VALUE, Long.MIN_VALUE, true),
      Arguments.of(Long.MIN_VALUE, Long.MIN_VALUE, 0, true)
    );
  }
}
