/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang.logic.univ.nor;

import com.google.common.base.Preconditions;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class NotTest {

  @Order(1)
  @ParameterizedTest
  @CsvSource({
    // a     q
    "true, false",
    "false, true"
  })
  public void testBehavior(boolean a, boolean q) {
    // Given
    Not not = new Not();
    not.setUp();

    not.init();

    // When
    not.getA().update(a);
    not.compute();

    // Then
    Assertions.assertThat(not.getQ().getValue()).isEqualTo(q);
  }

  @Order(2)
  @ParameterizedTest
  @MethodSource("histories")
  public void testBehavior(boolean[] a, boolean[] q) {
    Preconditions.checkArgument(a.length > 0);
    Preconditions.checkArgument(q.length > 0);
    Preconditions.checkArgument(q.length == a.length);

    // Given
    Not not = new Not();
    not.setUp();

    not.init();

    boolean[] actual = new boolean[q.length];

    // When
    for (int i = 0; i < a.length; i++) {
      not.getA().update(a[i]);

      not.compute();

      actual[i] = not.getQ().getValue();

      not.getQ().tick();
    }

    // Then
    Assertions.assertThat(actual).containsExactly(q);
  }

  public static Stream<Arguments> histories() {
    return Stream.of(
      // 1
      Arguments.of(new boolean[]{true, true}, new boolean[]{false, false}),
      // 2
      Arguments.of(new boolean[]{true, false}, new boolean[]{false, true}),
      // 3
      Arguments.of(new boolean[]{false, true}, new boolean[]{true, false}),
      // 4
      Arguments.of(new boolean[]{false, false}, new boolean[]{true, true}),
      // 5
      Arguments.of(new boolean[]{true, true, true}, new boolean[]{false, false, false}),
      // 6
      Arguments.of(new boolean[]{true, true, false}, new boolean[]{false, false, true}),
      // 7
      Arguments.of(new boolean[]{true, false, true}, new boolean[]{false, true, false}),
      // 8
      Arguments.of(new boolean[]{true, false, false}, new boolean[]{false, true, true}),
      // 9
      Arguments.of(new boolean[]{false, true, true}, new boolean[]{true, false, false}),
      // 10
      Arguments.of(new boolean[]{false, true, false}, new boolean[]{true, false, true}),
      // 11
      Arguments.of(new boolean[]{false, false, true}, new boolean[]{true, true, false}),
      // 12
      Arguments.of(new boolean[]{false, false, false}, new boolean[]{true, true, true})
    );
  }
}
