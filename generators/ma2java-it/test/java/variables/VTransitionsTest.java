/* (c) https://github.com/MontiCore/monticore */
package variables;

import Types.Direction;
import com.google.common.base.Preconditions;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

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

    // When
    Direction[] actual1 = new Direction[output1.length];
    Direction[] actual2 = new Direction[output2.length];

    for (int i = 0; i < input.length; i++) {
      // compute
      component.getI().update(input[i]);
      component.compute();

      // get output
      actual1[i] = component.getO1().getValue();
      actual2[i] = component.getO2().getValue();

      // tick
      component.getI().tick();
      component.tick();
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
        new Direction[]{ Direction.FORWARDS }
      ),
      // 2
      Arguments.of(
        new Direction[]{ Direction.FORWARDS, Direction.FORWARDS,
          Direction.FORWARDS },
        new Direction[]{ Direction.LEFT, Direction.FORWARDS,
          Direction.FORWARDS },
        new Direction[]{ Direction.FORWARDS, Direction.FORWARDS,
          Direction.FORWARDS }
      ),
      // 3
      Arguments.of(
        new Direction[]{ Direction.FORWARDS, Direction.BACKWARDS,
          Direction.LEFT, Direction.RIGHT, Direction.FORWARDS },
        new Direction[]{ Direction.LEFT, Direction.RIGHT,
          Direction.LEFT, Direction.RIGHT, Direction.FORWARDS },
        new Direction[]{ Direction.FORWARDS, Direction.BACKWARDS,
          Direction.LEFT, Direction.RIGHT, Direction.FORWARDS }
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
      Assertions.assertThat(component.v1).isEqualTo(Direction.LEFT);
      Assertions.assertThat(component.v2).isEqualTo(Direction.RIGHT);
      Assertions.assertThat(component.v3).isEqualTo(Direction.LEFT);
      Assertions.assertThat(component.v4).isEqualTo(Direction.RIGHT);
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
    component.getI().update(Direction.FORWARDS);
    component.compute();
    component.getI().tick();
    component.tick();
    component.getI().update(Direction.BACKWARDS);
    component.compute();
    component.getI().tick();
    component.tick();

    // Then
    assertAll(() -> {
      Assertions.assertThat(component.v1).isEqualTo(Direction.FORWARDS);
      Assertions.assertThat(component.v2).isEqualTo(Direction.BACKWARDS);
      Assertions.assertThat(component.v3).isEqualTo(Direction.LEFT);
      Assertions.assertThat(component.v4).isEqualTo(Direction.RIGHT);
    });
  }
}