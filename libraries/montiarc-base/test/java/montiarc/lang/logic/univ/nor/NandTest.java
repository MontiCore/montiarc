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

public class NandTest {

  @Order(1)
  @ParameterizedTest
  @CsvSource({
    // a     b     q
    "true, true, false",
    "true, false, true",
    "false, true, true",
    "false, false, true"
  })
  public void testBehavior(boolean a, boolean b, boolean q) {
    // Given
    Nand nand = new Nand();
    nand.setUp();

    nand.init();

    // When
    nand.getA().update(a);
    nand.getB().update(b);
    nand.compute();

    // Then
    Assertions.assertThat(nand.getQ().getValue()).isEqualTo(q);
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
    Nand nand = new Nand();
    nand.setUp();

    nand.init();

    boolean[] actual = new boolean[q.length];

    // When
    for (int i = 0; i < a.length; i++) {
      nand.getA().update(a[i]);
      nand.getB().update(b[i]);

      nand.compute();

      actual[i] = nand.getQ().getValue();

      nand.getQ().tick();
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
      Arguments.of(new boolean[]{true, false}, new boolean[]{true, false}, new boolean[]{false, true}),
      // 7
      Arguments.of(new boolean[]{true, false}, new boolean[]{false, true}, new boolean[]{true, true}),
      // 8
      Arguments.of(new boolean[]{true, false}, new boolean[]{false, false}, new boolean[]{true, true}),
      // 9
      Arguments.of(new boolean[]{false, true}, new boolean[]{true, true}, new boolean[]{true, false}),
      // 10
      Arguments.of(new boolean[]{false, true}, new boolean[]{true, false}, new boolean[]{true, true}),
      // 11
      Arguments.of(new boolean[]{false, true}, new boolean[]{false, true}, new boolean[]{true, false}),
      // 12
      Arguments.of(new boolean[]{false, true}, new boolean[]{false, false}, new boolean[]{true, true}),
      // 13
      Arguments.of(new boolean[]{false, false}, new boolean[]{true, true}, new boolean[]{true, true}),
      // 14
      Arguments.of(new boolean[]{false, false}, new boolean[]{true, false}, new boolean[]{true, true}),
      // 15
      Arguments.of(new boolean[]{false, false}, new boolean[]{false, true}, new boolean[]{true, true}),
      // 16
      Arguments.of(new boolean[]{false, false}, new boolean[]{false, false}, new boolean[]{true, true})
    );
  }
}
