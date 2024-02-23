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

public class XorTest {

  @Order(1)
  @ParameterizedTest
  @CsvSource({
    // a     b     q
    "true, true, false",
    "true, false, true",
    "false, true, true",
    "false, false, false"
  })
  public void testBehavior(boolean a, boolean b, boolean q) {
    // Given
    Xor xor = new Xor();
    xor.setUp();

    xor.init();

    // When
    xor.getA().update(a);
    xor.getB().update(b);
    xor.compute();

    // Then
    Assertions.assertThat(xor.getQ().getValue()).isEqualTo(q);
  }

  @Order(2)
  @ParameterizedTest
  @MethodSource("histories")
  public void testBehavior(boolean[] a, boolean[] b, boolean[] q) {
    Preconditions.checkArgument(a.length > 0);
    Preconditions.checkArgument(b.length > 0);
    Preconditions.checkArgument(q.length > 0);
    Preconditions.checkArgument(b.length == a.length);
    Preconditions.checkArgument(q.length == a.length);

    // Given
    Xor xor = new Xor();
    xor.setUp();

    xor.init();

    boolean[] actual = new boolean[q.length];

    // When
    for (int i = 0; i < a.length; i++) {
      xor.getA().update(a[i]);
      xor.getB().update(b[i]);

      xor.compute();

      actual[i] = xor.getQ().getValue();

      xor.getQ().tick();
    }

    // Then
    Assertions.assertThat(actual).containsExactly(q);
  }

  public static Stream<Arguments> histories() {
    return Stream.of(
      // 1
      Arguments.of(new boolean[]{true, true}, new boolean[]{true, true}, new boolean[]{false, false}),
      // 2
      Arguments.of(new boolean[]{true, true}, new boolean[]{true, false}, new boolean[]{false, true}),
      // 3
      Arguments.of(new boolean[]{true, true}, new boolean[]{false, true}, new boolean[]{true, false}),
      // 4
      Arguments.of(new boolean[]{true, true}, new boolean[]{false, false}, new boolean[]{true, true}),
      // 5
      Arguments.of(new boolean[]{true, false}, new boolean[]{true, true}, new boolean[]{false, true}),
      // 6
      Arguments.of(new boolean[]{true, false}, new boolean[]{true, false}, new boolean[]{false, false}),
      // 7
      Arguments.of(new boolean[]{true, false}, new boolean[]{false, true}, new boolean[]{true, true}),
      // 8
      Arguments.of(new boolean[]{true, false}, new boolean[]{false, false}, new boolean[]{true, false}),
      // 9
      Arguments.of(new boolean[]{false, true}, new boolean[]{true, true}, new boolean[]{true, false}),
      // 10
      Arguments.of(new boolean[]{false, true}, new boolean[]{true, false}, new boolean[]{true, true}),
      // 11
      Arguments.of(new boolean[]{false, true}, new boolean[]{false, true}, new boolean[]{false, false}),
      // 12
      Arguments.of(new boolean[]{false, true}, new boolean[]{false, false}, new boolean[]{false, true}),
      // 13
      Arguments.of(new boolean[]{false, false}, new boolean[]{true, true}, new boolean[]{true, true}),
      // 14
      Arguments.of(new boolean[]{false, false}, new boolean[]{true, false}, new boolean[]{true, false}),
      // 15
      Arguments.of(new boolean[]{false, false}, new boolean[]{false, true}, new boolean[]{false, true}),
      // 16
      Arguments.of(new boolean[]{false, false}, new boolean[]{false, false}, new boolean[]{false, false})
    );
  }
}
