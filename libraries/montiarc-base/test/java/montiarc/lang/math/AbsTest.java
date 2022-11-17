/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang.math;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class AbsTest {

  @ParameterizedTest
  @MethodSource("absIntegerTestProvider")
  public void testBehavior(int a, int r, boolean of) {
    // Given
    AbsI abs = new AbsI();
    abs.setUp();
    abs.init();
    abs.getA().update(a);

    // When
    abs.compute();

    // Then
    assertAll(
      () -> assertThat(abs.getR().getValue()).isEqualTo(r),
      () -> assertThat(abs.getOf().getValue()).isEqualTo(of)
    );
  }

  protected static Stream<Arguments> absIntegerTestProvider() {
    return Stream.of(
      Arguments.of(0, 0, false),
      Arguments.of(1, 1, false),
      Arguments.of(-1, 1, false),
      Arguments.of(Integer.MAX_VALUE, Integer.MAX_VALUE, false),
      Arguments.of(Integer.MIN_VALUE, Integer.MIN_VALUE, true)
    );
  }

  @ParameterizedTest
  @MethodSource("absLongTestProvider")
  public void testBehavior(long a, long r, boolean of) {
    // Given
    AbsL abs = new AbsL();
    abs.setUp();
    abs.init();
    abs.getA().update(a);

    // When
    abs.compute();

    // Then
    assertAll(
      () -> assertThat(abs.getR().getValue()).isEqualTo(r),
      () -> assertThat(abs.getOf().getValue()).isEqualTo(of)
    );
  }

  protected static Stream<Arguments> absLongTestProvider() {
    return Stream.of(

      Arguments.of(0, 0, false),
      Arguments.of(1, 1, false),
      Arguments.of(-1, 1, false),
      Arguments.of(Long.MAX_VALUE, Long.MAX_VALUE, false),
      Arguments.of(Long.MIN_VALUE, Long.MIN_VALUE, true)
    );
  }
}
