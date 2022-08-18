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

/**
 * The system under test is the component {@code PTransitions}. The black-box
 * tests ensure that the system which features configurable transitions produces
 * the expected outputs.
 */
public class PTransitionsTest {

  /**
   * Black-box test: Ensures that the automaton produces the expected output.
   *
   * @param cycles the number of computation cycles to run the simulation
   * @param p1 the first argument for instantiating the component
   * @param p2 the second argument for instantiating the component
   * @param p3 the third argument for instantiating the component
   * @param p4 the fourth argument for instantiating the component
   */
  @ParameterizedTest
  @MethodSource("argumentsProvider")
  @DisplayName("Component with parameters should produce expected output")
  public void shouldProduceExpectedOutput(int cycles,
                                          @NotNull Direction p1,
                                          @NotNull Direction p2,
                                          @NotNull Direction p3,
                                          @NotNull Direction p4) {
    Preconditions.checkNotNull(p1);
    Preconditions.checkNotNull(p2);
    Preconditions.checkNotNull(p3);
    Preconditions.checkNotNull(p4);
    Preconditions.checkArgument(cycles >= 1);

    Direction[] expected1 = new Direction[cycles];
    Direction[] expected2 = new Direction[cycles];
    for (int i = 0; i < cycles; i = i + 2) {
      expected1[i] = p3;
      expected2[i] = p4;
    }
    for (int i = 1; i < cycles; i = i + 2) {
      expected1[i] = p1;
      expected2[i] = p2;
    }

    // Given
    PTransitions component = new PTransitions(p1, p2, p3, p4);
    component.setUp();
    component.init();

    // When
    Direction[] actual1 = new Direction[cycles];
    Direction[] actual2 = new Direction[cycles];

    for (int i = 0; i < cycles; i++) {
      // update
      component.update();

      // compute
      component.compute();

      // add the current value after computation
      actual1[i] = component.getPortO1().getCurrentValue();
      actual2[i] = component.getPortO2().getCurrentValue();
    }

    // Then
    Assertions.assertThat(actual1).containsExactly(expected1);
    Assertions.assertThat(actual2).containsExactly(expected2);
  }

  /**
   * @return The number of computation cycles the simulation should run and
   * arguments for instantiating the component.
   */
  protected static Stream<Arguments> argumentsProvider() {
    return Stream.of(
        // 1
        Arguments.of(
            1, Direction.LEFT, Direction.FORWARDS,
            Direction.RIGHT, Direction.BACKWARDS
        ),
        // 2
        Arguments.of(
            10, Direction.LEFT, Direction.FORWARDS,
            Direction.RIGHT, Direction.BACKWARDS
        ),
        // 3
        Arguments.of(
            10, Direction.FORWARDS, Direction.FORWARDS,
            Direction.BACKWARDS, Direction.BACKWARDS
        )
    );
  }
}