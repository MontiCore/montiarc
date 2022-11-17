/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang.math;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SubtractTest {

  @ParameterizedTest
  @MethodSource("subtractIntegerTestProvider")
  public void testBehavior(int a, int b, int r, boolean of) {
    // Given
    SubtractI subtract = new SubtractI();
    subtract.setUp();
    subtract.init();
    subtract.getA().update(a);
    subtract.getB().update(b);

    // When
    subtract.compute();

    // Then
    assertAll(
      () -> assertThat(subtract.getR().getValue()).isEqualTo(r),
      () -> assertThat(subtract.getOf().getValue()).isEqualTo(of)
    );
  }

  protected static Stream<Arguments> subtractIntegerTestProvider() {
    return Stream.of(
      Arguments.of(0, 0, 0, false),
      Arguments.of(0, 1, -1, false),
      Arguments.of(0, -1, 1, false),
      Arguments.of(0, Integer.MAX_VALUE, -Integer.MAX_VALUE, false),
      Arguments.of(0, Integer.MIN_VALUE, Integer.MIN_VALUE, true),
      Arguments.of(1, 0, 1, false),
      Arguments.of(1, 1, 0, false),
      Arguments.of(1, -1, 2, false),
      Arguments.of(1, Integer.MAX_VALUE, Integer.MIN_VALUE + 2, false),
      Arguments.of(1, Integer.MIN_VALUE, Integer.MIN_VALUE + 1, true),
      Arguments.of(-1, 0, -1, false),
      Arguments.of(-1, 1, -2, false),
      Arguments.of(-1, -1, 0, false),
      Arguments.of(-1, Integer.MAX_VALUE, Integer.MIN_VALUE, false),
      Arguments.of(-1, Integer.MIN_VALUE, Integer.MAX_VALUE, false),
      Arguments.of(Integer.MAX_VALUE, 0, Integer.MAX_VALUE, false),
      Arguments.of(Integer.MAX_VALUE, 1, Integer.MAX_VALUE - 1, false),
      Arguments.of(Integer.MAX_VALUE, -1, Integer.MIN_VALUE, true),
      Arguments.of(Integer.MAX_VALUE, Integer.MAX_VALUE, 0, false),
      Arguments.of(Integer.MAX_VALUE, Integer.MIN_VALUE, -1, true),
      Arguments.of(Integer.MIN_VALUE, 0, Integer.MIN_VALUE, false),
      Arguments.of(Integer.MIN_VALUE, 1, Integer.MAX_VALUE, true),
      Arguments.of(Integer.MIN_VALUE, -1, Integer.MIN_VALUE + 1, false),
      Arguments.of(Integer.MIN_VALUE, Integer.MAX_VALUE, 1, true),
      Arguments.of(Integer.MIN_VALUE, Integer.MIN_VALUE, 0, false)
    );
  }

  @ParameterizedTest
  @MethodSource("subtractLongTestProvider")
  public void testBehavior(long a, long b, long r, boolean of) {
    // Given
    SubtractL subtract = new SubtractL();
    subtract.setUp();
    subtract.init();
    subtract.getA().update(a);
    subtract.getB().update(b);

    // When
    subtract.compute();

    // Then
    assertAll(
      () -> assertThat(subtract.getR().getValue()).isEqualTo(r),
      () -> assertThat(subtract.getOf().getValue()).isEqualTo(of)
    );
  }

  protected static Stream<Arguments> subtractLongTestProvider() {
    return Stream.of(
      Arguments.of(0, 0, 0, false),
      Arguments.of(0, 1, -1, false),
      Arguments.of(0, -1, 1, false),
      Arguments.of(0, Long.MAX_VALUE, -Long.MAX_VALUE, false),
      Arguments.of(0, Long.MIN_VALUE, Long.MIN_VALUE, true),
      Arguments.of(1, 0, 1, false),
      Arguments.of(1, 1, 0, false),
      Arguments.of(1, -1, 2, false),
      Arguments.of(1, Long.MAX_VALUE, Long.MIN_VALUE + 2, false),
      Arguments.of(1, Long.MIN_VALUE, Long.MIN_VALUE + 1, true),
      Arguments.of(-1, 0, -1, false),
      Arguments.of(-1, 1, -2, false),
      Arguments.of(-1, -1, 0, false),
      Arguments.of(-1, Long.MAX_VALUE, Long.MIN_VALUE, false),
      Arguments.of(-1, Long.MIN_VALUE, Long.MAX_VALUE, false),
      Arguments.of(Long.MAX_VALUE, 0, Long.MAX_VALUE, false),
      Arguments.of(Long.MAX_VALUE, 1, Long.MAX_VALUE - 1, false),
      Arguments.of(Long.MAX_VALUE, -1, Long.MIN_VALUE, true),
      Arguments.of(Long.MAX_VALUE, Long.MAX_VALUE, 0, false),
      Arguments.of(Long.MAX_VALUE, Long.MIN_VALUE, -1, true),
      Arguments.of(Long.MIN_VALUE, 0, Long.MIN_VALUE, false),
      Arguments.of(Long.MIN_VALUE, 1, Long.MAX_VALUE, true),
      Arguments.of(Long.MIN_VALUE, -1, Long.MIN_VALUE + 1, false),
      Arguments.of(Long.MIN_VALUE, Long.MAX_VALUE, 1, true),
      Arguments.of(Long.MIN_VALUE, Long.MIN_VALUE, 0, false)
    );
  }
}
