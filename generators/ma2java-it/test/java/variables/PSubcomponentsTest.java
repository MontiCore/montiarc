/* (c) https://github.com/MontiCore/monticore */
package variables;

import com.google.common.base.Preconditions;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import types.Direction;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;

/**
 * The system under test is the component {@code PSubcomponents}. The black-box
 * tests ensure that the system produces the expected outputs.
 */
public class PSubcomponentsTest {

  /**
   * Black-box test: Ensures that the configured subcomponents produces the
   * expected outputs.
   *
   * @param cycles the number of computation cycles to run the simulation
   * @param p1 the first argument for instantiating the component
   * @param p2 the second argument for instantiating the component
   * @param p3 the third argument for instantiating the component
   */
  @ParameterizedTest
  @MethodSource("argumentsProvider")
  @DisplayName("Component with parameters should produce expected output")
  public void shouldProduceExpectedOutput(int cycles,
                                          @NotNull Direction p1,
                                          @NotNull Direction p2,
                                          @NotNull Direction p3) {
    Preconditions.checkNotNull(p1);
    Preconditions.checkNotNull(p2);
    Preconditions.checkNotNull(p3);
    Preconditions.checkArgument(cycles >= 1);

    Direction[] expected1 = new Direction[cycles];
    Direction[] expected2 = new Direction[cycles];
    Direction[] expected3 = new Direction[cycles];
    Direction[] expected4 = new Direction[cycles];
    Direction[] expected5 = new Direction[cycles];

    for (int i = 0; i < cycles; i++) {
      expected1[i] = Direction.LEFT;
      expected2[i] = p1;
      expected3[i] = p2;
    }
    for (int i = 0; i < cycles; i = i + 2) {
      expected4[i] = Direction.FORWARDS;
      expected5[i] = Direction.BACKWARDS;
    }
    for (int i = 1; i < cycles; i = i + 2) {
      expected4[i] = p2;
      expected5[i] = p3;
    }

    //Given
    PSubcomponents component = new PSubcomponents(p1, p2, p3);
    component.setUp();
    component.init();

    // When
    Direction[] actual1 = new Direction[cycles];
    Direction[] actual2 = new Direction[cycles];
    Direction[] actual3 = new Direction[cycles];
    Direction[] actual4 = new Direction[cycles];
    Direction[] actual5 = new Direction[cycles];

    for (int i = 0; i < cycles; i++) {
      // compute
      component.compute();

      // get output
      actual1[i] = component.getO1().getValue();
      actual2[i] = component.getO2().getValue();
      actual3[i] = component.getO3().getValue();
      actual4[i] = component.getO4().getValue();
      actual5[i] = component.getO5().getValue();

      // tick
      component.tick();
    }

    // Then
    assertAll(() -> {
      Assertions.assertThat(actual1).containsExactly(expected1);
      Assertions.assertThat(actual2).containsExactly(expected2);
      Assertions.assertThat(actual3).containsExactly(expected3);
      Assertions.assertThat(actual4).containsExactly(expected4);
      Assertions.assertThat(actual5).containsExactly(expected5);
    });
  }

  /**
   * @return The number of computation cycles the simulation should run and
   * arguments for instantiating the component.
   */
  protected static Stream<Arguments> argumentsProvider() {
    return Stream.of(
      // 1
      Arguments.of(1, Direction.LEFT, Direction.LEFT, Direction.RIGHT),
      // 2
      Arguments.of(10, Direction.LEFT, Direction.LEFT, Direction.RIGHT),
      // 3
      Arguments.of(10, Direction.RIGHT, Direction.FORWARDS, Direction.BACKWARDS)
    );
  }
}