/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang.logic.gate;

import com.google.common.base.Preconditions;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class XnorTest {

  @Order(1)
  @ParameterizedTest
  @CsvSource({
    // a     b     q
    "true, true, true",
    "true, false, false",
    "false, true, false",
    "false, false, true"
  })
  public void testBehavior(boolean a, boolean b, boolean q) {
    // Given
    Xnor xnor = new Xnor();
    xnor.setUp();

    xnor.init();

    // When
    xnor.getA().update(a);
    xnor.getB().update(b);
    xnor.compute();

    // Then
    Assertions.assertThat(xnor.getQ().getValue()).isEqualTo(q);
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
    Xnor xnor = new Xnor();
    xnor.setUp();

    xnor.init();

    boolean[] actual = new boolean[q.length];

    // When
    for (int i = 0; i < a.length; i++) {
      xnor.getA().update(a[i]);
      xnor.getB().update(b[i]);

      xnor.compute();

      actual[i] = xnor.getQ().getValue();

      xnor.getQ().tick();
    }

    // Then
    Assertions.assertThat(actual).containsExactly(q);
  }

  public static Stream<Arguments> histories() {
    return Stream.of(
      // 1
      Arguments.of(new boolean[]{true, true}, new boolean[]{true, true}, new boolean[]{true, true}),
      // 2
      Arguments.of(new boolean[]{true, true}, new boolean[]{true, false}, new boolean[]{true, false}),
      // 3
      Arguments.of(new boolean[]{true, true}, new boolean[]{false, true}, new boolean[]{false, true}),
      // 4
      Arguments.of(new boolean[]{true, true}, new boolean[]{false, false}, new boolean[]{false, false}),
      // 5
      Arguments.of(new boolean[]{true, false}, new boolean[]{true, true}, new boolean[]{true, false}),
      // 6
      Arguments.of(new boolean[]{true, false}, new boolean[]{true, false}, new boolean[]{true, true}),
      // 7
      Arguments.of(new boolean[]{true, false}, new boolean[]{false, true}, new boolean[]{false, false}),
      // 8
      Arguments.of(new boolean[]{true, false}, new boolean[]{false, false}, new boolean[]{false, true}),
      // 9
      Arguments.of(new boolean[]{false, true}, new boolean[]{true, true}, new boolean[]{false, true}),
      // 10
      Arguments.of(new boolean[]{false, true}, new boolean[]{true, false}, new boolean[]{false, false}),
      // 11
      Arguments.of(new boolean[]{false, true}, new boolean[]{false, true}, new boolean[]{true, true}),
      // 12
      Arguments.of(new boolean[]{false, true}, new boolean[]{false, false}, new boolean[]{true, false}),
      // 13
      Arguments.of(new boolean[]{false, false}, new boolean[]{true, true}, new boolean[]{false, false}),
      // 14
      Arguments.of(new boolean[]{false, false}, new boolean[]{true, false}, new boolean[]{false, true}),
      // 15
      Arguments.of(new boolean[]{false, false}, new boolean[]{false, true}, new boolean[]{true, false}),
      // 16
      Arguments.of(new boolean[]{false, false}, new boolean[]{false, false}, new boolean[]{true, true})
    );
  }
}
