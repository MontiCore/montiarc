/* (c) https://github.com/MontiCore/monticore */
package automata;

import com.google.common.base.Preconditions;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import types.Direction;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * The systems under test are the components {@code Parameter} and {@code
 * Parameters}. The black-box tests ensure that the parameterized systems
 * produces the expected outputs in order.
 */
public class ParameterTest {

  /**
   * Black-box test: Ensures that the automaton produces the expected output,
   * which is the provided parameter for every computation cycle.
   *
   * @param p the argument for instantiating the component
   */
  @ParameterizedTest
  @EnumSource(Direction.class)
  @DisplayName("Component with parameter should produce expected outputs.")
  @Order(1)
  public void shouldProduceExpectedOutput(@NotNull Direction p) {
    Preconditions.checkNotNull(p);

    // Given
    Parameter component = new Parameter(p);
    component.setUp();
    component.init();
    int cycles = 3;

    // When
    List<Direction> actual = new ArrayList<>(cycles + 1);
    actual.add(component.getPortO().getCurrentValue());
    for (int i = 0; i < cycles; i++) {
      component.compute();
      component.update();

      // add the current value after computation
      actual.add(component.getPortO().getCurrentValue());
    }

    // Then
    Assertions.assertThat(actual).containsOnly(p);
  }

  /**
   * Black-box test: Ensures that the automaton produces the expected output,
   * which is the provided parameters for every computation cycle.
   *
   * @param p1 the first argument for instantiating the component
   * @param p2 the second argument for instantiating the component
   */
  @ParameterizedTest
  @MethodSource("argumentsProvider")
  @DisplayName("Component with two parameters should produce expected outputs.")
  @Order(2)
  public void shouldProduceExpectedOutput(@NotNull Direction p1,
                                          @NotNull Direction p2) {
    Preconditions.checkNotNull(p1);
    Preconditions.checkNotNull(p2);

    // Given
    Parameters component = new Parameters(p1, p2);
    component.setUp();
    component.init();
    int cycles = 3;

    // When
    List<Direction> a1 = new ArrayList<>(cycles + 1);
    List<Direction> a2 = new ArrayList<>(cycles + 1);
    a1.add(component.getPortO1().getCurrentValue());
    a2.add(component.getPortO2().getCurrentValue());
    for (int i = 0; i < cycles; i++) {
      component.compute();
      component.update();

      // add the current value after computation
      a1.add(component.getPortO1().getCurrentValue());
      a2.add(component.getPortO2().getCurrentValue());
    }

    // Then
    Assertions.assertThat(a1.size()).isEqualTo(4);
    Assertions.assertThat(a2.size()).isEqualTo(4);
    Assertions.assertThat(a1).containsOnly(p1);
    Assertions.assertThat(a2).containsOnly(p2);
  }

  /**
   * @return The number of computation cycles the simulation should run and an
   * array of the expected outputs.
   */
  protected static Stream<Arguments> argumentsProvider() {
    return Stream.of(
        Arguments.of(Direction.FORWARDS, Direction.FORWARDS),
        Arguments.of(Direction.FORWARDS, Direction.BACKWARDS),
        Arguments.of(Direction.LEFT, Direction.RIGHT)
    );
  }
}