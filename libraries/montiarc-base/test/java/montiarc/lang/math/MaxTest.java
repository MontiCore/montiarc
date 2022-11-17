/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang.math;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class MaxTest {

  @ParameterizedTest
  @MethodSource("maxIntegerTestProvider")
  public void testBehavior(int a, int b, int r) {
    // Given
    MaxI max = new MaxI();
    max.setUp();
    max.init();
    max.getA().update(a);
    max.getB().update(b);

    // When
    max.compute();

    // Then
    assertThat(max.getR().getValue()).isEqualTo(r);
  }

  protected static Stream<Arguments> maxIntegerTestProvider() {
    return Stream.of(
      Arguments.of(0, 0, 0),
      Arguments.of(0, 1, 1),
      Arguments.of(0, -1, 0),
      Arguments.of(0, Integer.MAX_VALUE, Integer.MAX_VALUE),
      Arguments.of(0, Integer.MIN_VALUE, 0),
      Arguments.of(1, 0, 1),
      Arguments.of(1, 1, 1),
      Arguments.of(1, -1, 1),
      Arguments.of(1, Integer.MAX_VALUE, Integer.MAX_VALUE),
      Arguments.of(1, Integer.MIN_VALUE, 1),
      Arguments.of(-1, 0, 0),
      Arguments.of(-1, -1, -1),
      Arguments.of(-1, 1, 1),
      Arguments.of(-1, Integer.MAX_VALUE, Integer.MAX_VALUE),
      Arguments.of(-1, Integer.MIN_VALUE, -1),
      Arguments.of(Integer.MAX_VALUE, 0, Integer.MAX_VALUE),
      Arguments.of(Integer.MAX_VALUE, 1, Integer.MAX_VALUE),
      Arguments.of(Integer.MAX_VALUE, -1, Integer.MAX_VALUE),
      Arguments.of(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE),
      Arguments.of(Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE),
      Arguments.of(Integer.MIN_VALUE, 0, 0),
      Arguments.of(Integer.MIN_VALUE, 1, 1),
      Arguments.of(Integer.MIN_VALUE, -1, -1),
      Arguments.of(Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE),
      Arguments.of(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE)
    );
  }

  @ParameterizedTest
  @MethodSource("maxLongTestProvider")
  public void testBehavior(long a, long b, long r) {
    // Given
    MaxL max = new MaxL();
    max.setUp();
    max.init();
    max.getA().update(a);
    max.getB().update(b);

    // When
    max.compute();

    // Then
    assertThat(max.getR().getValue()).isEqualTo(r);
  }

  protected static Stream<Arguments> maxLongTestProvider() {
    return Stream.of(
      Arguments.of(0, 0, 0),
      Arguments.of(0, 1, 1),
      Arguments.of(0, -1, 0),
      Arguments.of(0, Long.MAX_VALUE, Long.MAX_VALUE),
      Arguments.of(0, Long.MIN_VALUE, 0),
      Arguments.of(1, 0, 1),
      Arguments.of(1, 1, 1),
      Arguments.of(1, -1, 1),
      Arguments.of(1, Long.MAX_VALUE, Long.MAX_VALUE),
      Arguments.of(1, Long.MIN_VALUE, 1),
      Arguments.of(-1, 0, 0),
      Arguments.of(-1, 1, 1),
      Arguments.of(-1, -1, -1),
      Arguments.of(-1, Long.MAX_VALUE, Long.MAX_VALUE),
      Arguments.of(-1, Long.MIN_VALUE, -1),
      Arguments.of(Long.MAX_VALUE, 0, Long.MAX_VALUE),
      Arguments.of(Long.MAX_VALUE, -1, Long.MAX_VALUE),
      Arguments.of(Long.MAX_VALUE, 1, Long.MAX_VALUE),
      Arguments.of(Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE),
      Arguments.of(Long.MAX_VALUE, Long.MIN_VALUE, Long.MAX_VALUE),
      Arguments.of(Long.MIN_VALUE, 0, 0),
      Arguments.of(Long.MIN_VALUE, 1, 1),
      Arguments.of(Long.MIN_VALUE, -1, -1),
      Arguments.of(Long.MIN_VALUE, Long.MAX_VALUE, Long.MAX_VALUE),
      Arguments.of(Long.MIN_VALUE, Long.MIN_VALUE, Long.MIN_VALUE)
    );
  }
}
