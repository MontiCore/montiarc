/* (c) https://github.com/MontiCore/monticore */
package automata.nondeterminism;

import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import automata.nondeterminism.Transition.States;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

/**
 * The system under test is the component {@code Transition}. The white-box
 * tests ensure that the automaton is in the expected state after computation.
 */
public class TransitionTest {

  /**
   * White-box test: Given the input ensures that the automaton transitions
   * into the expected states and produces the expected output
   *
   * @param inputs     the inputs given to the component under test in order, its
   *                   length should match the number of expected output messages
   * @param expOutputs the outputs expected in order, its length should match the
   *                   number of expected output messages
   * @param expStates  the states the automaton is expected to visit in order
   */
  @ParameterizedTest
  @MethodSource("expBehaviorProvider")
  @DisplayName("Source should visit expected states")
  public void shouldBehaveAsExpected(int[] inputs, int[] expOutputs,
                                     @NotNull States[] expStates) {
    Preconditions.checkNotNull(inputs);
    Preconditions.checkNotNull(expOutputs);
    Preconditions.checkNotNull(expStates);
    Preconditions.checkArgument(expOutputs.length == inputs.length);
    Preconditions.checkArgument(expStates.length == inputs.length + 1);
    int[] actOutputs = new int[expOutputs.length];
    States[] actStates = new States[expStates.length];

    // Given
    Transition component = new Transition();
    component.setUp();

    // When
    component.init();
    // get the initial state
    actStates[0] = component.getCurrentState();
    for (int i = 0; i < inputs.length; i++) {
      // compute
      component.getI().update(inputs[i]);
      component.compute();

      // get the current state
      actStates[i + 1] = component.getCurrentState();
      actOutputs[i] = component.getO().getValue();

      // tick
      component.tick();
    }

    // Then
    assertAll(
      () -> assertThat(actStates).containsExactly(expStates),
      () -> assertThat(actOutputs).containsExactly(expOutputs)
    );
  }

  /**
   * @return The input, expected output, and the expected state transitions.
   */
  protected static Stream<Arguments> expBehaviorProvider() {
    return Stream.of(
      Arguments.of(new int[]{1, 1}, new int[]{1, 1}, new States[] { States.start, States.s1, States.end1}),
      Arguments.of(new int[]{2, 2}, new int[]{2, 2}, new States[] { States.start, States.s2, States.end2}),
      Arguments.of(new int[]{3, 3}, new int[]{3, 1}, new States[] { States.start, States.s3, States.end1}),
      Arguments.of(new int[]{4, 4}, new int[]{4, 2}, new States[] { States.start, States.s4_1, States.end2}),
      Arguments.of(new int[]{5, 5}, new int[]{5, 1}, new States[] { States.start, States.s5_1, States.end1})
    );
  }
}
