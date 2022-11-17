/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang.math;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class AddTest {

  @ParameterizedTest
  @MethodSource("addIntegerTestProvider")
  public void testBehavior(int a, int b, int r, boolean of) {
    // Given
    AddI add = new AddI();
    add.setUp();
    add.init();
    add.getA().update(a);
    add.getB().update(b);

    // When
    add.compute();

    // Then
    assertAll(
      () -> assertThat(add.getR().getValue()).isEqualTo(r),
      () -> assertThat(add.getOf().getValue()).isEqualTo(of)
    );
  }

  protected static Stream<Arguments> addIntegerTestProvider() {
    return Stream.of(
      Arguments.of(0, 0, 0, false),
      Arguments.of(0, 1, 1, false),
      Arguments.of(0, -1, -1, false),
      Arguments.of(0, Integer.MAX_VALUE, Integer.MAX_VALUE, false),
      Arguments.of(0, Integer.MIN_VALUE, Integer.MIN_VALUE, false),
      Arguments.of(1, 0, 1, false),
      Arguments.of(1, 1, 2, false),
      Arguments.of(1, -1, 0, false),
      Arguments.of(1, Integer.MAX_VALUE, Integer.MIN_VALUE, true),
      Arguments.of(1, Integer.MIN_VALUE, Integer.MIN_VALUE + 1, false),
      Arguments.of(-1, 0, -1, false),
      Arguments.of(-1, 1, 0, false),
      Arguments.of(-1, -1, -2, false),
      Arguments.of(-1, Integer.MAX_VALUE, Integer.MAX_VALUE - 1, false),
      Arguments.of(-1, Integer.MIN_VALUE, Integer.MAX_VALUE, true),
      Arguments.of(Integer.MAX_VALUE, 0, Integer.MAX_VALUE, false),
      Arguments.of(Integer.MAX_VALUE, 1, Integer.MIN_VALUE, true),
      Arguments.of(Integer.MAX_VALUE, -1, Integer.MAX_VALUE - 1, false),
      Arguments.of(Integer.MAX_VALUE, Integer.MAX_VALUE, -2, true),
      Arguments.of(Integer.MAX_VALUE, Integer.MIN_VALUE, -1, false),
      Arguments.of(Integer.MIN_VALUE, 0, Integer.MIN_VALUE, false),
      Arguments.of(Integer.MIN_VALUE, 1, Integer.MIN_VALUE + 1, false),
      Arguments.of(Integer.MIN_VALUE, -1, Integer.MAX_VALUE, true),
      Arguments.of(Integer.MIN_VALUE, Integer.MAX_VALUE, -1, false),
      Arguments.of(Integer.MIN_VALUE, Integer.MIN_VALUE, 0, true)
    );
  }

  @ParameterizedTest
  @MethodSource("addLongTestProvider")
  public void testBehavior(long a, long b, long r, boolean of) {
    // Given
    AddL add = new AddL();
    add.setUp();
    add.init();
    add.getA().update(a);
    add.getB().update(b);

    // When
    add.compute();

    // Then
    assertAll(
      () -> assertThat(add.getR().getValue()).isEqualTo(r),
      () -> assertThat(add.getOf().getValue()).isEqualTo(of)
    );
  }

  protected static Stream<Arguments> addLongTestProvider() {
    return Stream.of(
      Arguments.of(0, 0, 0, false),
      Arguments.of(0, 1, 1, false),
      Arguments.of(0, -1, -1, false),
      Arguments.of(0, Long.MAX_VALUE, Long.MAX_VALUE, false),
      Arguments.of(0, Long.MIN_VALUE, Long.MIN_VALUE, false),
      Arguments.of(1, 0, 1, false),
      Arguments.of(1, 1, 2, false),
      Arguments.of(1, -1, 0, false),
      Arguments.of(1, Long.MAX_VALUE, Long.MIN_VALUE, true),
      Arguments.of(1, Long.MIN_VALUE, Long.MIN_VALUE + 1, false),
      Arguments.of(-1, 0, -1, false),
      Arguments.of(-1, 1, 0, false),
      Arguments.of(-1, -1, -2, false),
      Arguments.of(-1, Long.MAX_VALUE, Long.MAX_VALUE - 1, false),
      Arguments.of(-1, Long.MIN_VALUE, Long.MAX_VALUE, true),
      Arguments.of(Long.MAX_VALUE, 0, Long.MAX_VALUE, false),
      Arguments.of(Long.MAX_VALUE, 1, Long.MIN_VALUE, true),
      Arguments.of(Long.MAX_VALUE, -1, Long.MAX_VALUE - 1, false),
      Arguments.of(Long.MAX_VALUE, Long.MAX_VALUE, -2, true),
      Arguments.of(Long.MAX_VALUE, Long.MIN_VALUE, -1, false),
      Arguments.of(Long.MIN_VALUE, 0, Long.MIN_VALUE, false),
      Arguments.of(Long.MIN_VALUE, 1, Long.MIN_VALUE + 1, false),
      Arguments.of(Long.MIN_VALUE, -1, Long.MAX_VALUE, true),
      Arguments.of(Long.MIN_VALUE, Long.MAX_VALUE, -1, false),
      Arguments.of(Long.MIN_VALUE, Long.MIN_VALUE, 0, true)
    );
  }
}
