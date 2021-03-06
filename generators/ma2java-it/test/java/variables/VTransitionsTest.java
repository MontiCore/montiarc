/* (c) https://github.com/MontiCore/monticore */
package variables;

import com.google.common.base.Preconditions;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import types.Direction;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;

/**
 * The system under test is the component {@code VTransitions}. The black-box
 * tests ensure that the system produces the expected outputs in order. The
 * white-box tests ensure that the automaton's variables are in the expected
 * state after computation.
 */
public class VTransitionsTest {

  /**
   * Black-box test: Ensures that the automaton produces the expected output.
   *
   * @param input the inputs given to the component under test in order
   * @param output1 the expected output messages on the first output port in
   * order, its length should match the number of input messages
   * @param output2 the expected output messages on the second output port in
   * order, its length should match the number of input messages
   */
  @ParameterizedTest
  @Order(3)
  @MethodSource("inputAndExpectedOutputProvider")
  @DisplayName("Component with variables should produce expected outputs")
  public void shouldProduceExpectedOutputs(@NotNull Direction[] input,
                                           @NotNull Direction[] output1,
                                           @NotNull Direction[] output2) {
    Preconditions.checkNotNull(input);
    Preconditions.checkNotNull(output1);
    Preconditions.checkNotNull(output2);
    Preconditions.checkArgument(input.length >= 1);
    Preconditions.checkArgument(output1.length >= 1);
    Preconditions.checkArgument(output2.length >= 1);
    Preconditions.checkArgument(input.length == output1.length);
    Preconditions.checkArgument(input.length == output2.length);

    // Given
    VTransitions component = new VTransitions();
    component.setUp();
    component.init();

    // provide initial input
    component.getPortI().setNextValue(input[0]);
    component.getPortI().update();

    // When
    Direction[] actual1 = new Direction[output1.length];
    Direction[] actual2 = new Direction[output2.length];

    // add the initial value
    actual1[0] = component.getPortO1().getCurrentValue();
    actual2[0] = component.getPortO2().getCurrentValue();

    for (int i = 1; i < input.length; i++) {
      component.getPortI().setNextValue(input[i]);
      component.compute();
      component.getPortI().update();
      component.update();

      // add the current value after computation
      actual1[i] = component.getPortO1().getCurrentValue();
      actual2[i] = component.getPortO2().getCurrentValue();
    }

    // Then
    Assertions.assertThat(actual1).containsExactly(output1);
    Assertions.assertThat(actual2).containsExactly(output2);
  }

  /**
   * @return An array of input messages and an array of the expected outputs the
   * automaton should produce for the given input
   */
  protected static Stream<Arguments> inputAndExpectedOutputProvider() {
    return Stream.of(
        // 1
        Arguments.of(
            new Direction[]{ Direction.FORWARDS },
            new Direction[]{ Direction.LEFT },
            new Direction[]{ Direction.RIGHT }
        ),
        // 2
        Arguments.of(
            new Direction[]{ Direction.FORWARDS, Direction.FORWARDS,
                Direction.FORWARDS },
            new Direction[]{ Direction.LEFT, Direction.LEFT,
                Direction.FORWARDS },
            new Direction[]{ Direction.RIGHT, Direction.FORWARDS,
                Direction.FORWARDS }
        ),
        // 3
        Arguments.of(
            new Direction[]{ Direction.FORWARDS, Direction.BACKWARDS,
                Direction.LEFT, Direction.RIGHT, Direction.FORWARDS },
            new Direction[]{ Direction.LEFT, Direction.LEFT,
                Direction.RIGHT, Direction.LEFT, Direction.RIGHT },
            new Direction[]{ Direction.RIGHT, Direction.FORWARDS,
                Direction.BACKWARDS, Direction.LEFT, Direction.RIGHT }
        )
    );
  }

  /**
   * White-box test: Ensures that the variables are initialized as expected.
   */
  @Test
  @Order(1)
  @DisplayName("Should initialize variables as expected")
  public void shouldInitializeVariablesAsExpected() {
    // Given
    VTransitions component = new VTransitions();

    // When
    component.setUp();
    component.init();

    // Then
    assertAll(() -> {
      Assertions.assertThat(((VTransitionsImpl)component.behaviorImpl).v1).isEqualTo(Direction.LEFT);
      Assertions.assertThat(((VTransitionsImpl)component.behaviorImpl).v2).isEqualTo(Direction.RIGHT);
      Assertions.assertThat(((VTransitionsImpl)component.behaviorImpl).v3).isEqualTo(Direction.LEFT);
      Assertions.assertThat(((VTransitionsImpl)component.behaviorImpl).v4).isEqualTo(Direction.RIGHT);
    });
  }

  /**
   * White-box test: Ensures that the automaton updates variables as expected.
   */
  @Test
  @Order(2)
  @DisplayName("Should update variables as expected")
  public void shouldUpdateVariablesAsExpected() {
    // Given
    VTransitions component = new VTransitions();
    component.setUp();
    component.init();

    // When
    component.getPortI().setNextValue(Direction.FORWARDS);
    component.getPortI().update();
    component.compute();
    component.getPortI().setNextValue(Direction.BACKWARDS);
    component.getPortI().update();
    component.compute();

    // Then
    assertAll(() -> {
      Assertions.assertThat(((VTransitionsImpl)component.behaviorImpl).v1).isEqualTo(Direction.FORWARDS);
      Assertions.assertThat(((VTransitionsImpl)component.behaviorImpl).v2).isEqualTo(Direction.BACKWARDS);
      Assertions.assertThat(((VTransitionsImpl)component.behaviorImpl).v3).isEqualTo(Direction.LEFT);
      Assertions.assertThat(((VTransitionsImpl)component.behaviorImpl).v4).isEqualTo(Direction.RIGHT);
    });
  }
}