/* (c) https://github.com/MontiCore/monticore */
package automata;

import com.google.common.base.Preconditions;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import types.Direction;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;

/**
 * The system under test is the component {@code Variables}. The black-box tests
 * ensure that the system produces the expected outputs in order. The white-box
 * tests ensure that the automaton's variables are in the expected state after
 * computation.
 */
public class VariablesTest {

  /**
   * Black-box test: Ensures that the automaton produces the expected output.
   *
   * @param input the inputs given to the component under test in order, its
   * length should match the number of expected output messages
   * @param output the expected output messages in order
   */
  @ParameterizedTest
  @MethodSource("inputAndExpectedOutputProvider")
  @DisplayName("Component with variables should produce expected outputs")
  public void shouldProduceExpectedOutputs(@NotNull Direction[] input,
                                           @NotNull Direction[] output) {
    Preconditions.checkNotNull(input);
    Preconditions.checkNotNull(output);
    Preconditions.checkArgument(input.length >= 1);
    Preconditions.checkArgument(output.length >= 1);
    Preconditions.checkArgument(input.length == output.length);

    // Given
    Variables component = new Variables(Direction.LEFT);
    component.setUp();
    component.init();

    // provide initial input
    component.getPortI().setNextValue(input[0]);
    component.getPortI().update();

    // When
    List<Direction> actual = new ArrayList<>(output.length);

    // add the initial value
    actual.add(component.getPortO().getCurrentValue());
    for (int i = 1; i < input.length; i++) {
      component.getPortI().setNextValue(input[i]);
      component.compute();
      component.getPortI().update();
      component.update();

      // add the current value after computation
      actual.add(component.getPortO().getCurrentValue());
    }

    // Then
    Assertions.assertThat(actual).containsExactly(output);
  }

  /**
   * @return An array of input messages and an array of the expected outputs the
   * automaton should produce for the given input
   */
  protected static Stream<Arguments> inputAndExpectedOutputProvider() {
    return Stream.of(
        Arguments.of(new Direction[]{ Direction.FORWARDS, Direction.FORWARDS },
                     new Direction[]{ Direction.RIGHT, Direction.FORWARDS }),
        Arguments.of(new Direction[]{ Direction.BACKWARDS,
                         Direction.BACKWARDS },
                     new Direction[]{ Direction.RIGHT, Direction.BACKWARDS }),
        Arguments.of(new Direction[]{ Direction.LEFT, Direction.LEFT },
                     new Direction[]{ Direction.RIGHT, Direction.LEFT }),
        Arguments.of(new Direction[]{ Direction.RIGHT, Direction.RIGHT },
                     new Direction[]{ Direction.RIGHT, Direction.RIGHT }),
        Arguments.of(new Direction[]{ Direction.FORWARDS, Direction.BACKWARDS,
                         Direction.LEFT, Direction.RIGHT },
                     new Direction[]{ Direction.RIGHT, Direction.FORWARDS,
                         Direction.BACKWARDS, Direction.LEFT })
    );
  }

  /**
   * White-box test: Ensures that the automaton updates the component's
   * variables as expected.
   *
   * @param input the input to the component under test, a single message which,
   * based on its concrete value, is stored in a component variable
   */
  @ParameterizedTest
  @EnumSource(Direction.class)
  @DisplayName("Should only write to expected variables")
  public void shouldOnlyWriteToExpectedVariables(@NotNull Direction input) {
    Preconditions.checkNotNull(input);

    // Given
    Variables component = new Variables(Direction.LEFT);
    component.setUp();
    component.init();

    Direction v1 = input == Direction.FORWARDS ? Direction.FORWARDS :
        component.v1;
    Direction v2 = input == Direction.BACKWARDS ? Direction.BACKWARDS :
        component.v2;
    Direction v3 = input == Direction.LEFT ? Direction.LEFT : component.v3;
    Direction v4 = input == Direction.RIGHT ? Direction.RIGHT : component.v4;

    // When
    component.getPortI().setNextValue(input);
    component.getPortI().update();
    component.compute();

    // Then
    assertAll(() -> {
      Assertions.assertThat(((VariablesImpl) component.behaviorImpl).v1).isEqualTo(v1);
      Assertions.assertThat(((VariablesImpl) component.behaviorImpl).v2).isEqualTo(v2);
      Assertions.assertThat(((VariablesImpl) component.behaviorImpl).v3).isEqualTo(v3);
      Assertions.assertThat(((VariablesImpl) component.behaviorImpl).v4).isEqualTo(v4);
    });
  }
}